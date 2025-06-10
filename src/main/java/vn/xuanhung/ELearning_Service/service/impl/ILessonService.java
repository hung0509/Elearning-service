package vn.xuanhung.ELearning_Service.service.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
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
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.LessonRepository;
import vn.xuanhung.ELearning_Service.service.LessonService;
import vn.xuanhung.ELearning_Service.specification.LessonSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ILessonService implements LessonService {
    LessonRepository lessonRepository;

    KafkaTemplate<String, Object> kafkaTemplate;
    YouTube youtube;
    ModelMapper modelMapper;

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

        try {
            lesson.setUrlLesson(uploadVideo(req.getUrlLesson(), req.getLessonName(), req.getDescription()));
        }catch (Exception e){
            e.printStackTrace();
        }

        lessonRepository.save(lesson);
        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(lesson.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());
        return ApiResponse.<LessonResponse>builder()
                .result(modelMapper.map(lesson, LessonResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(Integer id) {
        //Chuyeenr trang thai thoi
        log.info("***Log lesson service - get delete lesson ***");
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

        lesson.setIsActive(AppConstant.STATUS_UNACTIVE);
        lessonRepository.save(lesson);

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

    private String uploadVideo(MultipartFile filePath, String title, String description) throws Exception {
        // Tạo Metadata cho video
        Video video = new Video();

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("public"); // Mặc định là public
        video.setStatus(status);

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        video.setSnippet(snippet);

        // Lưu file tạm từ MultipartFile
        File tempFile = File.createTempFile("upload", filePath.getOriginalFilename());
        filePath.transferTo(tempFile);

        // Chuẩn bị nội dung file để upload
        InputStreamContent mediaContent = new InputStreamContent("video/*", new FileInputStream(tempFile));

        try {
            // Thực hiện upload video
            YouTube.Videos.Insert request = youtube.videos()
                    .insert("snippet,status", video, mediaContent);
            Video response = request.execute();

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
}
