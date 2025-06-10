package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.CommentRequest;
import vn.xuanhung.ELearning_Service.dto.response.CommentResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserCommentViewResponse;
import vn.xuanhung.ELearning_Service.service.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comments")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentController {
    CommentService commentService;
    SimpMessagingTemplate messagingTemplate;

    @GetMapping()
    @ResponseBody
    public ApiResponsePagination<List<UserCommentViewResponse>> getAll(@ModelAttribute CommentRequest req) {
        log.info("*Log controller comment - get all comments*");
        return commentService.getAll(req);
    }

    @PostMapping
    @ResponseBody
    public ApiResponse<Void> sendMessage(@RequestBody CommentRequest message) {
        log.info("*Log controller comment - call kafka comment*");
        commentService.sendMessage(message);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ApiResponse<Integer> delete(@PathVariable Integer id) {
        log.info("*Log controller comment - delete comment*");

        return commentService.deleteCommentById(id);
    }

    //    -------------- WebSocket API ----------------
    @MessageMapping("/send-message")
    public void broadcastGroupMessage(@Payload UserCommentViewResponse message) {
        log.info("*Log controller comment - receive comment*");
        messagingTemplate.convertAndSend("/topic/comment/" + message.getLessonId(), message);
    }

    @MessageMapping("/register")
    public void addUser(@Payload CommentRequest id,
                           SimpMessageHeaderAccessor headerAccessor) {
        log.info("*Log controller comment - register comment*");
        // Add user in web socket session
        headerAccessor.getSessionAttributes().put("username", id);
    }
}
