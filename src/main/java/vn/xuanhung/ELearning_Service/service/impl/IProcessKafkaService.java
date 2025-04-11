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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.KafkaUploadVideoDto;
import vn.xuanhung.ELearning_Service.entity.Course;
import vn.xuanhung.ELearning_Service.entity.Lesson;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.CourseRepository;
import vn.xuanhung.ELearning_Service.repository.LessonRepository;

import java.io.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IProcessKafkaService {
    CourseRepository courseRepository;
    LessonRepository lessonRepository;
    YouTube youtube;
    AmazonS3 amazonS3;
    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;


    @KafkaListener(topics = AppConstant.Topic.VIDEO_TOPIC, groupId = "gr-sync-order", containerFactory = "kafkaListenerContainerFactory")
    private void uploadVideo(ConsumerRecord<String, KafkaUploadVideoDto> consumerRecord, Acknowledgment acknowledgment) throws Exception {
        // Tạo Metadata cho video
        log.info("UserService: Received message from Kafka topic: " + AppConstant.Topic.VIDEO_TOPIC);

        // Extract key and value from the ConsumerRecord
        String key = consumerRecord.key(); // could be null
        KafkaUploadVideoDto kafkaUploadVideoDto = consumerRecord.value();
        log.info("Received message with key: " + key);
        log.info("Received message with value: " + kafkaUploadVideoDto);
        File temFile = downloadS3ObjectToTempFile(kafkaUploadVideoDto.getS3Url());
        try {
            Video video = new Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private"); // Mặc định là public
            video.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(kafkaUploadVideoDto.getTitle());
            snippet.setDescription(kafkaUploadVideoDto.getDescription());
            video.setSnippet(snippet);


            // Chuẩn bị nội dung file để upload
            InputStreamContent mediaContent = new InputStreamContent("video/*", new FileInputStream(temFile));

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
                courseRepository.save(course);
            }else if(kafkaUploadVideoDto.getLessonId() != null){
                Lesson lesson = lessonRepository.findById(kafkaUploadVideoDto.getLessonId())
                        .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
                lesson.setUrlLesson(url);
                lessonRepository.save(lesson);
            }

            acknowledgment.acknowledge();
       } catch (Exception e) {
            throw new RuntimeException("Error uploading video to YouTube", e);
       } finally {
            if(temFile != null && temFile.exists()){
                temFile.delete();
            }
            amazonS3.deleteObject(AWS_BUCKET, kafkaUploadVideoDto.getS3Url());
        }
    }

    private File downloadS3ObjectToTempFile(String s3Key) throws IOException {
        S3Object s3Object = amazonS3.getObject(AWS_BUCKET, s3Key);
        InputStream inputStream = s3Object.getObjectContent();

        File tempFile = File.createTempFile("temp-video-", ".mp4");
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
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
