package vn.xuanhung.ELearning_Service.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CategoryRequest;
import vn.xuanhung.ELearning_Service.dto.request.CourseCacheUpdateEvent;
import vn.xuanhung.ELearning_Service.dto.request.CourseDocumentRequest;
import vn.xuanhung.ELearning_Service.dto.response.CategoryResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseDocumentResponse;
import vn.xuanhung.ELearning_Service.entity.CourseDocument;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.CourseDocumentRepository;
import vn.xuanhung.ELearning_Service.service.CourseDocumentService;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICourseDocumentService implements CourseDocumentService {
    CourseDocumentRepository courseDocumentRepository;

    S3Client s3Client;
    ModelMapper modelMapper;
    KafkaTemplate<String, Object> kafkaTemplate;

    @NonFinal
    @Value("${aws.bucket}")
    String AWS_BUCKET;

    @NonFinal
    @Value("${aws.folder}")
    String AWS_FOLDER;

    @Override
    @Transactional
    public ApiResponse<CourseDocumentResponse> save(CourseDocumentRequest req) {
        log.info("*Log service documents - save document*");
        if(req.getId() != null){
            CourseDocument entity = courseDocumentRepository.findById(req.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
            modelMapper.map(req, entity);

            entity = courseDocumentRepository.save(entity);
            log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
            kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                    .courseId(entity.getCourseId())
                    .action(AppConstant.ACTION.INVALIDATE)
                    .build());

            return ApiResponse.<CourseDocumentResponse>builder()
                    .result(modelMapper.map(entity, CourseDocumentResponse.class))
                    .build();
        }else{
            CourseDocument entity = modelMapper.map(req, CourseDocument.class);
            try{
                entity.setDocumentUrl(uploadFile(req.getDocumentUrl()));
            }catch(Exception e){
                e.printStackTrace();
            }

            String originalFilename = req.getDocumentUrl().getOriginalFilename(); // Ví dụ: "abc.docx"
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
            entity.setFileType(extension);
            entity.setIsActive("Y");

            entity = courseDocumentRepository.save(entity);

            log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
            kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                    .courseId(entity.getCourseId())
                    .action(AppConstant.ACTION.INVALIDATE)
                    .build());

            return ApiResponse.<CourseDocumentResponse>builder()
                    .result(modelMapper.map(entity, CourseDocumentResponse.class))
                    .build();
        }
    }

    @Override
    public ApiResponse<CourseDocumentResponse> remove(Integer id) {
        CourseDocument document = courseDocumentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_EXIST));

        document.setIsActive(AppConstant.STATUS_UNACTIVE);
        document = courseDocumentRepository.save(document);

        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(document.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());

        return ApiResponse.<CourseDocumentResponse>builder()
                .result(modelMapper.map(document, CourseDocumentResponse.class))
                .build();
    }


    private String uploadFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType); // Hoặc loại nội dung phù hợp khác

        String keyName = AWS_FOLDER + "/" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(AWS_BUCKET)
                .key(keyName)
                .contentType(contentType)
                .build();

        PutObjectResponse future = s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(inputStream, file.getSize())
        );   //Đẩy hình ảnh lên trên bucket

//        URL url = s3AsyncClient.getUrl(AWS_BUCKET, keyName);
//        //Ở đây đang để ở public access
//        //nếu block access đi ta cần cấu hình IAM role...(Tìm hiểu thêm)
//        return url.toString();

        return String.format("https://%s.s3.amazonaws.com/%s", AWS_BUCKET, keyName);
    }
}
