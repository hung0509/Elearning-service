package vn.xuanhung.ELearning_Service.service.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CourseCacheUpdateEvent;
import vn.xuanhung.ELearning_Service.dto.request.LessonRequest;
import vn.xuanhung.ELearning_Service.dto.request.LessonUpdateRequest;
import vn.xuanhung.ELearning_Service.dto.response.LessonResponse;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.CourseRepository;
import vn.xuanhung.ELearning_Service.repository.LessonRepository;
import vn.xuanhung.ELearning_Service.service.LessonService;
import vn.xuanhung.ELearning_Service.specification.LessonSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ILessonService implements LessonService {
    LessonRepository lessonRepository;
    CourseRepository courseRepository;

    KafkaTemplate<String, Object> kafkaTemplate;
    YouTube youtube;
    ModelMapper modelMapper;
    EntityManager entityManager;

    @Override
    public ApiResponsePagination<List<LessonResponse>> findAll(LessonRequest request) {
        log.info("***Log lesson service - get all lesson by pagination***");
        Pageable pageable = PageRequest.of(request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Specification<Lesson> spec = LessonSpecification.getSpecification(request);
        Page<Lesson> lessons = lessonRepository.findAll(spec, pageable);

        List<Lesson> data = lessons.getContent();
        return ApiResponsePagination.<List<LessonResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item, LessonResponse.class)).toList())
                .totalItems(lessons.getTotalElements())
                .pageSize(request.getPageSize())
                .currentPage(request.getPage())
                .totalPages(lessons.getTotalPages())
                .build();
    }

    @Override
    public ApiResponse<LessonResponse> findById(Integer id) {
        log.info("***Log lesson service - get lesson by ID***");
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
        return ApiResponse.<LessonResponse>builder()
                .result(modelMapper.map(lesson, LessonResponse.class))
                .build();
    }

    @Override
    public ApiResponse<LessonResponse> save(LessonRequest req) {
        log.info("***Log lesson service - get save lesson ***");
        Lesson lesson = modelMapper.map(req, Lesson.class);
        lesson.setIsActive("Y");

        String playlistId = getPlaylistId(req.getCourseId());
        if(playlistId != null){
            lesson.setPlayListId(playlistId);
        }

        try {
            lesson.setUrlLesson(uploadVideo(req.getUrlLesson(), req.getLessonName(), req.getDescription(), playlistId));
        }catch (Exception e){
            e.printStackTrace();
        }

        lesson = lessonRepository.save(lesson);

        //Cập nhật COurse
        Course course = courseRepository.findById(lesson.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        course.setQuantity(course.getQuantity().add(BigDecimal.ONE));
        course.setCourseDuration(course.getCourseDuration().add(lesson.getLessonTime()));
        course = courseRepository.save(course);

        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(course.getId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());

        LessonResponse lessonResponse = modelMapper.map(lesson, LessonResponse.class);
        return ApiResponse.<LessonResponse>builder()
                .result(lessonResponse)
                .build();
    }

    private String getPlaylistId(Integer courseId) {
        String sql = "SELECT playlist_id FROM d_course WHERE course_id = :courseId";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("courseId", courseId);

        Object result = query.getSingleResult();

        if (result != null) {
            return result.toString(); // ✅ Lấy giá trị thật sự từ kết quả truy vấn
        }

        return null;
    }


    @Override
    public ApiResponse<String> deleteById(Integer id) {
        //Chuyeenr trang thai thoi
        log.info("***Log lesson service - get delete lesson ***");
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

        lesson.setIsActive(AppConstant.STATUS_UNACTIVE);
        lessonRepository.save(lesson);

        Course course = courseRepository.findById(lesson.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        course.setQuantity(course.getQuantity().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
        course.setCourseDuration(course.getCourseDuration().subtract(lesson.getLessonTime()).max(BigDecimal.ZERO));
        course = courseRepository.save(course);


        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(lesson.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());
        return ApiResponse.<String>builder()
                .result("Lesson status update successful!!!")
                .build();
    }

    @Override
    public ApiResponse<LessonResponse> update(LessonUpdateRequest lessonRequest, Integer id) {
        log.info("***Log lesson service - get update lesson ***");
        Lesson lesson = null;
        if(id != null) {
            lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
        }

        modelMapper.map(lessonRequest, lesson);
        lesson = lessonRepository.save(lesson);

        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(lesson.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());
        return ApiResponse.<LessonResponse>builder()
                .result(modelMapper.map(lesson, LessonResponse.class))
                .build();
    }

    private String uploadVideo(MultipartFile filePath, String title, String description, String playlistId) throws Exception {
        // Tạo Metadata cho video
        Video video = new Video();

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("public"); // Mặc định là public
        video.setStatus(status);

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        video.setSnippet(snippet);


        // Làm sạch tên file gốc để tránh ký tự đặc biệt
        String originalFilename = filePath.getOriginalFilename();
        String safeFilename = sanitizeFilename(originalFilename);  // Xử lý tên an toàn

        if (safeFilename.length() > 100) {
            safeFilename = safeFilename.substring(safeFilename.length() - 100);
        }


        // Lưu file tạm từ MultipartFile
        File tempFile = File.createTempFile("upload", safeFilename);
        filePath.transferTo(tempFile);

        // Chuẩn bị nội dung file để upload
        InputStreamContent mediaContent = new InputStreamContent("video/*", new FileInputStream(tempFile));

        try {
            // Thực hiện upload video
            YouTube.Videos.Insert request = youtube.videos()
                    .insert("snippet,status", video, mediaContent);
            Video response = request.execute();

            addVideoToPlaylist(playlistId, response.getId());

            // Trả về URL video đã upload
            System.out.println("Video uploaded successfully. Video ID: " + response.getId());
            return "https://www.youtube.com/embed/" + response.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error uploading video to YouTube", e);
        } finally {
            // Xóa file tạm
            tempFile.delete();
        }
    }

    // Hàm loại bỏ ký tự không hợp lệ khỏi tên file
    private String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";

        String normalized = Normalizer.normalize(filename, Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
        ascii = ascii.replaceAll("\\s+", "-");
        ascii = ascii.replaceAll("[^a-zA-Z0-9._-]", "");
        ascii = ascii.replaceAll("[-_]{2,}", "-"); // Gộp gạch
        return ascii;
    }



    public void addVideoToPlaylist(String playlistId, String videoId) {
        try {
            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playlistId);

            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);
            playlistItemSnippet.setResourceId(resourceId);

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            youtube.playlistItems().insert("snippet", playlistItem).execute();

        } catch (Exception e) {
            throw new RuntimeException("Error add video to playlist", e);
        }
    }
}
