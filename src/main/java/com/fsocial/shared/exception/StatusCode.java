package com.fsocial.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum StatusCode implements CodeEnum {

    // ===== System / Common =====
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa được xử lý.", HttpStatus.INTERNAL_SERVER_ERROR),
    OK(200, "OK", HttpStatus.OK),
    HTTPMETHOD_NOT_SUPPORTED(231, "HTTP method not supported", HttpStatus.METHOD_NOT_ALLOWED),
    PARAMATER_NOT_FOUND(220, "Paramater Not Found", HttpStatus.BAD_REQUEST),
    METHOD_NOT_INSTALLED(230, "Method Not installed", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(300, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_MEDIA_TYPE(304, "Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    NOT_FOUND(404, "Không tìm thấy.", HttpStatus.NOT_FOUND),
    ENPOINTMENT_NOT_FOUND(210, "Enpointment Not Found", HttpStatus.NOT_FOUND),
    NOT_CONTENT(211, "Not Content", HttpStatus.BAD_REQUEST),
    IOEXCEPTION(311, "IOException", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== Auth & Account =====
    ACCOUNT_EXISTED(464, "Tài khoản đã tồn tại.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(465, "Email đã tồn tại.", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_EXISTED(467, "Tài khoản không tồn tại.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(468, "Tài khoản chưa được xác thực.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(208, "Client not found", HttpStatus.NOT_FOUND),
    OTP_INVALID(463, "Mã OTP không hợp lệ.", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(470, "Tên đăng nhập đã tồn tại.", HttpStatus.BAD_REQUEST),
    DUPLICATION(471, "Tên đăng nhập hoặc Email đã tồn tại.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(472, "Sai tên đăng nhập hoặc mật khẩu.", HttpStatus.BAD_REQUEST),
    WEAK_SECRET_KEY(473, "Signer Key không đủ mạnh.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(700, "Token hết thời hạn.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(701, "Token không hợp lệ.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(702, "Mật khẩu không đúng.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(703, "Không có quyền truy cập.", HttpStatus.UNAUTHORIZED),
    ACCOUNT_BANNED(601, "Account banned.", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_FOUND(310, "Role not found", HttpStatus.NOT_FOUND),
    LOCAL_AUTH_NOT_FOUND(704, "Tài khoản này không đăng nhập bằng mật khẩu.", HttpStatus.BAD_REQUEST),
    EMAIL_IS_NOT_VERIFY(309, "Email mail is not verify", HttpStatus.BAD_REQUEST),
    INVALID_GOOGLE_TOKEN(403, "Invalid or expired Google token", HttpStatus.FORBIDDEN),

    // ===== Account Validation (Bean Validation message keys — names are load-bearing, do not rename) =====
    INVALID_EMAIL(900, "Email không hợp lệ.", HttpStatus.BAD_REQUEST),
    REQUIRED_EMAIL(901, "Email không được để trống.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(902, "Mật khẩu phải có ít nhất 8 ký tự, chứa ít nhất một chữ cái và một chữ số.", HttpStatus.BAD_REQUEST),
    REQUIRED_PASSWORD(903, "Mật khẩu không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_OTP(904, "OTP không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_USERNAME(905, "Tên đăng nhập không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_FIRSTNAME(906, "Tên không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_LASTNAME(907, "Họ không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_TYPE_REQUEST(908, "Loại yêu cầu không được để trống.", HttpStatus.BAD_REQUEST),
    REQUIRED_TOKEN(909, "Token không được để trống.", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(910, "Tên đăng nhập có ít nhất 6 ký tự.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_REQUEST(911, "Loại yêu cầu không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(912, "Độ tuổi không phù hợp.", HttpStatus.BAD_REQUEST),
    INVALID_NAME(913, "Tên không được chứa chữ số.", HttpStatus.BAD_REQUEST),

    // ===== Account Success Responses =====
    SUCCESS(200, "Thao tác thành công.", HttpStatus.OK),
    ACCOUNT_REGISTERED(200, "Tài khoản đã được đăng ký thành công.", HttpStatus.OK),
    OTP_SENT(200, "OTP đã được gửi tới email của bạn.", HttpStatus.OK),
    OTP_VALID(200, "OTP là hợp lệ.", HttpStatus.OK),
    PASSWORD_RESET_SUCCESS(200, "Mật khẩu đã được đặt lại thành công.", HttpStatus.OK),
    VALID(200, "Thông tin hợp lệ.", HttpStatus.OK),
    INVALID(200, "Thông tin không hợp lệ.", HttpStatus.OK),
    PASSWORD_CHANGED(200, "Mật khẩu đã được thay đổi.", HttpStatus.OK),

    // ===== Post =====
    CREATE_POST_SUCCESS(201, "Create post success", HttpStatus.CREATED),
    CREATE_POST_FAILED(216, "Create post failed", HttpStatus.BAD_REQUEST),
    UPDATE_POST_SUCCESS(217, "Update post success", HttpStatus.OK),
    DELETE_POST_SUCCESS(213, "Delete post success", HttpStatus.OK),
    POST_NOT_FOUND(209, "Post not found", HttpStatus.NOT_FOUND),
    FEED_EMPTY(214, "Feed is empty", HttpStatus.OK),
    INTEREST_NOT_FOUND(215, "User interest data not found", HttpStatus.NOT_FOUND),
    HASHTAG_NOT_FOUND(318, "Hashtag not found", HttpStatus.NOT_FOUND),
    HASHTAG_ALREADY_EXISTS(319, "Hashtag already exists", HttpStatus.CONFLICT),

    // ===== Comment =====
    CREATE_COMMENT_SUCCESS(205, "Create comment success", HttpStatus.CREATED),
    CREATE_COMMENT_FAILED(206, "Create comment failed", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(212, "Comment Not Found", HttpStatus.NOT_FOUND),
    REPLY_COMMENT_NOT_FOUND(306, "Reply Comment Not Found", HttpStatus.NOT_FOUND),

    // ===== Media / File =====
    FILE_NOT_FOUND(202, "File not found", HttpStatus.NOT_FOUND),
    UPLOAD_FILE_SUCCESS(203, "Upload file success", HttpStatus.OK),
    UPLOAD_FILE_FAILED(204, "Upload file failed", HttpStatus.BAD_REQUEST),
    UPLOAD_MEDIA_FAILED(218, "Upload media failed", HttpStatus.BAD_REQUEST),
    UPLOAD_AVATAR_FAIL(312, "Upload avatar fail", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(308, "File size exceeds the allowed limit", HttpStatus.PAYLOAD_TOO_LARGE),

    // ===== Chat =====
    CONVERSATION_NOT_FOUND(313, "Conversation not found", HttpStatus.NOT_FOUND),
    NOT_CONVERSATION_MEMBER(314, "You are not a member of this conversation", HttpStatus.FORBIDDEN),
    MESSAGE_SEND_FAILED(315, "Send message failed", HttpStatus.BAD_REQUEST),
    AI_SERVICE_FAILED(316, "AI service failed", HttpStatus.BAD_GATEWAY),

    // ===== Notification / FAQ / Complaint / Term of Service =====
    SEND_MAIL_FAIL(500, "Send mail fail", HttpStatus.INTERNAL_SERVER_ERROR),
    TERM_OF_SERVICE_NOT_FOUND(305, "Term of service not found", HttpStatus.NOT_FOUND),
    COMPLAIN_NOT_FOUND(307, "Complain Not Found", HttpStatus.NOT_FOUND),
    FAQ_NOT_FOUND(317, "FAQ not found", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    StatusCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
