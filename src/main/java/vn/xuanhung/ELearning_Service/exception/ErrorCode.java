package vn.xuanhung.ELearning_Service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //Lỗi hệ thống
    NOT_ENOUGH_INFO(7777, "Không đầy đủ thông tin!", HttpStatus.BAD_REQUEST),
    SYSTEM_ERROR(6666, "Có lỗi hệ thống xảy ra!", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXIST(1015, "Quyền không tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1001, "Người dùng không tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1002, "Vai trò không tồn tại", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_EXIST(1003, "Địa chỉ không tồn tại!", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXIST(1004, "Ảnh không tồn tại!", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_EXIST(1005, "Phiếu giảm giá không tồn tại!", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXIST(1006, "Loại khóa học không tồn tại!", HttpStatus.BAD_REQUEST),
    PAYMENT_CREATE_ERROR(1014, "Thanh toán thất bái!", HttpStatus.BAD_REQUEST),
    ARTICLE_NOT_EXIST(1015, "Bài viết không tồn tại!", HttpStatus.BAD_REQUEST),
    LESSON_NOT_EXIST(1016, "Bài học không tồn tại!", HttpStatus.BAD_REQUEST),
    COURSE_NOT_EXIST(1017, "Khóa học không tồn tại!", HttpStatus.BAD_REQUEST),
    QUIZ_NOT_EXIST(1018, "Bài ập không tồn tại!", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_EXIST(1019, "Tài liệu không tồn tại!", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_ENOUGH(1010, "Product not enough", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(9999, "Tài khoản chưa được xác thức!", HttpStatus.UNAUTHORIZED),
    NOT_VALID_FORMAT_IMAGE(2001, "Loại tệp đính kèm không hợp lệ!", HttpStatus.BAD_REQUEST),
    EXISTED_USERNAME(4001, "Tên đăng nhập này đã tồn tại!", HttpStatus.BAD_REQUEST),
    EXISTED_EMAIL(4002, "Email này đã tồn tại!", HttpStatus.BAD_REQUEST),
    LINK_EXPIRE(8888, "Link đã hết hạn!", HttpStatus.BAD_REQUEST),
    DECODE_NOT_AVAILABLE(8887, "Decode not available", HttpStatus.BAD_REQUEST),
    ERROR_SQL(1234, "Có lỗi xảy ra ở đây!", HttpStatus.BAD_REQUEST),
    ERROR_UPLOAD(1235, "Có lỗi xảy ra khi tải tệp ở đây!", HttpStatus.BAD_REQUEST),
    UPLOAD_S3_FAIL(1236,"Có lỗi xảy ra khi tải tệp lên AWS ở đây!", HttpStatus.BAD_REQUEST),
    COMMENT_FAIL(12377, "Bình luận của bạn đã có lỗi xảy ra!", HttpStatus.BAD_REQUEST),
    QUESTION_EMPTY(12378, "Không tìm thấy câu hỏi nào!", HttpStatus.BAD_REQUEST),
    ANSWER_EMPTY(12378, "Không tìm thấy câu trả lời nào!", HttpStatus.BAD_REQUEST),
    INVALID_ANSWER_COUNT(12400, "Have one correct answer", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVE(12222, "Tài khoản chưa được kích hoạt!", HttpStatus.OK),
    USER_NOT_REGISTER(12225, "Người dùng chưa đăng ký khóa học này", HttpStatus.BAD_REQUEST)
    ;

    int code;
    String message;
    HttpStatus status;
}
