package vn.xuanhung.ELearning_Service.service.impl;

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
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.CommentRequest;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserCommentViewResponse;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;
import vn.xuanhung.ELearning_Service.entity.view.UserCommentView;
import vn.xuanhung.ELearning_Service.repository.CommentRepository;
import vn.xuanhung.ELearning_Service.repository.view.UserCommentViewRepository;
import vn.xuanhung.ELearning_Service.service.CommentService;
import vn.xuanhung.ELearning_Service.specification.CourseHeaderSpecification;
import vn.xuanhung.ELearning_Service.specification.UserCommentViewSpecification;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICommentSerivce implements CommentService {
    UserCommentViewRepository userCommentViewRepository;
    ModelMapper modelMapper;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMessage(CommentRequest commentRequest) {
        log.info("***Log comment service - send message ***");
        try {
            //Sending the message to kafka topic queue
            kafkaTemplate.send(AppConstant.Topic.COMMENT_TOPIC, commentRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponsePagination<List<UserCommentViewResponse>> getAll(CommentRequest req) {
        log.info("***Log comment service - get comment ***");
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<UserCommentView> spec = UserCommentViewSpecification.getSpecification(req);

        Page<UserCommentView> page = userCommentViewRepository.findAll(spec, pageable);
        List<UserCommentView> list = page.getContent();

        return ApiResponsePagination.<List<UserCommentViewResponse>>builder()
                .result(list.stream().map(item -> modelMapper.map(item, UserCommentViewResponse.class)).toList())
                .pageSize(req.getPageSize())
                .totalPages(page.getTotalPages())
                .currentPage(req.getPage())
                .totalItems(page.getTotalElements())
                .build();
    }


}
