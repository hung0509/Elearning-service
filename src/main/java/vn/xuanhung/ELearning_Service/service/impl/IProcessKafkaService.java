package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CommentRequest;
import vn.xuanhung.ELearning_Service.dto.request.KafkaUploadVideoDto;
import vn.xuanhung.ELearning_Service.dto.response.CommentResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserCommentViewResponse;
import vn.xuanhung.ELearning_Service.entity.Comment;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.CommentRepository;
import vn.xuanhung.ELearning_Service.repository.CourseRepository;
import vn.xuanhung.ELearning_Service.repository.LessonRepository;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IProcessKafkaService {
    SimpMessagingTemplate template;
    CourseRepository courseRepository;
    LessonRepository lessonRepository;
    CommentRepository commentRepository;
    UserInfoRepository userInfoRepository;

    ModelMapper modelMapper;
    YouTube youtube;
    S3Client s3Client;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @KafkaListener(
            topics = AppConstant.Topic.VIDEO_TOPIC,
            groupId = "gr-sync-order",
            containerFactory = "kafkaListenerContainerFactory")
    private void uploadVideo(ConsumerRecord<String, KafkaUploadVideoDto> consumerRecord, Acknowledgment acknowledgment)
            throws Exception {
        // Tạo Metadata cho video
        log.info("UserService: Received message from Kafka topic: " + AppConstant.Topic.VIDEO_TOPIC);

        // Extract key and value from the ConsumerRecord
        String key = consumerRecord.key(); // could be null
        KafkaUploadVideoDto kafkaUploadVideoDto = consumerRecord.value();
        log.info("Received message with key: " + key);
        log.info("Received message with value: " + kafkaUploadVideoDto);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(AWS_BUCKET)
                .key(kafkaUploadVideoDto.getS3Url())  // ví dụ: "folder1/image.png"
                .build();
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);


        InputStreamContent mediaContent = new InputStreamContent("video/*", s3Object);
        mediaContent.setLength(s3Object.response().contentLength());
        try {
            Video video = new Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private"); // Mặc định là public
            video.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(kafkaUploadVideoDto.getTitle());
            snippet.setDescription(kafkaUploadVideoDto.getDescription());
            video.setSnippet(snippet);

            // Thực hiện upload video
            YouTube.Videos.Insert request = youtube.videos()
                    .insert("snippet,status", video, mediaContent);
            Video response = request.execute();

            // Trả về URL video đã upload
            System.out.println("Video uploaded successfully. Video ID: " + response.getId());
            String url =  "https://www.youtube.com/embed/" + response.getId();

            addVideoToPlaylist(kafkaUploadVideoDto.getPlaylistId(), response.getId());

            if(kafkaUploadVideoDto.getCourseId() != null){
                Course course = courseRepository.findById(kafkaUploadVideoDto.getCourseId())
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
                course.setTrailer(url);
                course.setPlayListId(kafkaUploadVideoDto.getPlaylistId());
                course.setCourseDuration(getCourseDuration(kafkaUploadVideoDto.getCourseId()));
                courseRepository.save(course);
            }else if(kafkaUploadVideoDto.getLessonId() != null){
                Lesson lesson = lessonRepository.findById(kafkaUploadVideoDto.getLessonId())
                        .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
                lesson.setUrlLesson(url);
                lesson.setPlayListId(kafkaUploadVideoDto.getPlaylistId());
                lessonRepository.save(lesson);
            }

            acknowledgment.acknowledge();
       } catch (Exception e) {
            throw new RuntimeException("Error uploading video to YouTube", e);
       } finally {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(AWS_BUCKET)
                    .key(kafkaUploadVideoDto.getS3Url())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
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

    private BigDecimal getCourseDuration(Integer courseId){
        List<Lesson> lessons = lessonRepository.findAllByCourseId(courseId);

        BigDecimal courseDuration = BigDecimal.ZERO;
        if(!lessons.isEmpty()){
            for(Lesson lesson: lessons) {
                courseDuration = courseDuration.add(lesson.getLessonTime());
            }
        }

        return courseDuration;
    }

    @KafkaListener(
            topics = AppConstant.Topic.COMMENT_TOPIC,
            groupId = "gr-sync-order", containerFactory = "kafkaListenerContainerFactory"
    )
    public void sendMessage(ConsumerRecord<String, CommentRequest> consumerRecord, Acknowledgment acknowledgment) {
        log.info("UserService: Received message from Kafka topic: " + AppConstant.Topic.COMMENT_TOPIC);

        // Extract key and value from the ConsumerRecord
        String key = consumerRecord.key(); // could be null
        CommentRequest message = consumerRecord.value();
        log.info("Received message with key: " + key);
        log.info("Received message with value: " + message);


        try{
            Comment comment = modelMapper.map(message, Comment.class);
            comment = commentRepository.save(comment);
            log.info("Comment: {}", comment);

            UserInfo userInfo = userInfoRepository.findById(comment.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

            UserCommentViewResponse userCommentViewResponse = UserCommentViewResponse
                    .builder()
                    .id(comment.getId())
                    .userId(userInfo.getId())
                    .content(comment.getContent())
                    .firstName(userInfo.getFirstName())
                    .lastName(userInfo.getLastName())
                    .avatar(userInfo.getAvatar())
                    .updatedAt(comment.getUpdatedAt())
                    .createdAt(comment.getCreatedAt())
                    .lessonId(comment.getLessonId())
                    .isActive(comment.getIsActive())
                    .build();

            acknowledgment.acknowledge();

            template.convertAndSend("/topic/comment/" + message.getLessonId(), userCommentViewResponse);
        }catch(Exception e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
    //    @KafkaListener(topics = AppConstant.Topic.VIDEO_TOPIC, groupId = "gr-sync-order", containerFactory = "kafkaListenerContainerFactory")
//    private void uploadVideoAtYoutube(ConsumerRecord<String, KafkaUploadVideoDto> consumerRecord, Acknowledgment acknowledgment) throws Exception {
//        // Tạo Metadata cho video
//        log.info("UserService: Received message from Kafka topic: " + AppConstant.Topic.VIDEO_TOPIC);
//
//        // Extract key and value from the ConsumerRecord
//        String key = consumerRecord.key(); // could be null
//        KafkaUploadVideoDto kafkaUploadVideoDto = consumerRecord.value();
//        log.info("Received message with key: " + key);
//        log.info("Received message with value: " + kafkaUploadVideoDto);
//
//        InputStreamContent mediaContent = new InputStreamContent(
//                kafkaUploadVideoDto.getVideo().getContentType(), // ví dụ: video/mp4
//                new BufferedInputStream( kafkaUploadVideoDto.getVideo().getInputStream())
//        );
//        mediaContent.setLength(kafkaUploadVideoDto.getVideo().getSize());
//        try {
//            Video video = new Video();
//
//            VideoStatus status = new VideoStatus();
//            status.setPrivacyStatus("private"); // Mặc định là public
//            video.setStatus(status);
//
//            VideoSnippet snippet = new VideoSnippet();
//            snippet.setTitle(kafkaUploadVideoDto.getTitle());
//            snippet.setDescription(kafkaUploadVideoDto.getDescription());
//            video.setSnippet(snippet);
//
//            // Thực hiện upload video
//            YouTube.Videos.Insert request = youtube.videos()
//                    .insert("snippet,status", video, mediaContent);
//            Video response = request.execute();
//
//            // Trả về URL video đã upload
//            System.out.println("Video uploaded successfully. Video ID: " + response.getId());
//            String url =  "https://www.youtube.com/embed/" + response.getId();
//
//            addVideoToPlaylist(kafkaUploadVideoDto.getPlaylistId(), response.getId());
//
//            if(kafkaUploadVideoDto.getCourseId() != null){
//                Course course = courseRepository.findById(kafkaUploadVideoDto.getCourseId())
//                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
//                course.setTrailer(url);
//                course.setPlayListId(kafkaUploadVideoDto.getPlaylistId());
//                courseRepository.save(course);
//            }else if(kafkaUploadVideoDto.getLessonId() != null){
//                Lesson lesson = lessonRepository.findById(kafkaUploadVideoDto.getLessonId())
//                        .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
//                lesson.setUrlLesson(url);
//                lesson.setPlayListId(kafkaUploadVideoDto.getPlaylistId());
//                lessonRepository.save(lesson);
//            }
//
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            throw new RuntimeException("Error uploading video to YouTube", e);
//        }
//    }
}
