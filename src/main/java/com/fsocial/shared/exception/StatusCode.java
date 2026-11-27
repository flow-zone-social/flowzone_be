package com.fsocial.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum StatusCode implements CodeEnum {

    // ===== System / Common =====
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
    OK(200, "OK", HttpStatus.OK),
    HTTP_METHOD_NOT_SUPPORTED(231, "HTTP method not supported.", HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_PARAMETER(220, "Invalid parameter.", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER(230, "Missing required parameter.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(300, "Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_MEDIA_TYPE(304, "Unsupported media type.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    NOT_FOUND(404, "Resource not found.", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND(210, "Endpoint not found.", HttpStatus.NOT_FOUND),
    CONTENT_REQUIRED(211, "Content is required.", HttpStatus.BAD_REQUEST),
    IO_EXCEPTION(311, "I/O error.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== Auth & User =====
    USER_ALREADY_EXISTS(464, "User already exists.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(465, "Email already exists.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(468, "User is not authenticated.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(208, "User not found.", HttpStatus.NOT_FOUND),
    OTP_INVALID(463, "Invalid OTP.", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(470, "Username already exists.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME_OR_EMAIL(471, "Username or email already exists.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(472, "Incorrect username or password.", HttpStatus.BAD_REQUEST),
    WEAK_SECRET_KEY(473, "Signer key is not strong enough.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(700, "Token has expired.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(701, "Invalid token.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(702, "Incorrect password.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(703, "Access denied.", HttpStatus.UNAUTHORIZED),
    USER_BANNED(601, "User is banned.", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_FOUND(310, "Role not found.", HttpStatus.NOT_FOUND),
    LOCAL_AUTH_NOT_FOUND(704, "This account does not use password login.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(309, "Email is not verified.", HttpStatus.BAD_REQUEST),
    INVALID_GOOGLE_TOKEN(403, "Invalid or expired Google token.", HttpStatus.FORBIDDEN),

    // ===== User Validation (Bean Validation message keys — names are load-bearing, do not rename) =====
    INVALID_EMAIL(900, "Invalid email.", HttpStatus.BAD_REQUEST),
    REQUIRED_EMAIL(901, "Email is required.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(902, "Password must be at least 8 characters and contain at least one letter and one digit.", HttpStatus.BAD_REQUEST),
    REQUIRED_PASSWORD(903, "Password is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_OTP(904, "OTP is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_USERNAME(905, "Username is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_FIRSTNAME(906, "First name is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_LASTNAME(907, "Last name is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_TYPE_REQUEST(908, "Request type is required.", HttpStatus.BAD_REQUEST),
    REQUIRED_TOKEN(909, "Token is required.", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(910, "Username must be at least 6 characters.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_REQUEST(911, "Invalid request type.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(912, "Age is not eligible.", HttpStatus.BAD_REQUEST),
    INVALID_NAME(913, "Name must not contain digits.", HttpStatus.BAD_REQUEST),

    // ===== Auth & User Success Responses =====
    SUCCESS(200, "Operation successful.", HttpStatus.OK),
    USER_REGISTERED(200, "User registered successfully.", HttpStatus.OK),
    OTP_SENT(200, "OTP has been sent to your email.", HttpStatus.OK),
    OTP_VALID(200, "OTP is valid.", HttpStatus.OK),
    PASSWORD_RESET_SUCCESS(200, "Password has been reset successfully.", HttpStatus.OK),
    VALID(200, "Information is valid.", HttpStatus.OK),
    INVALID(200, "Information is invalid.", HttpStatus.OK),
    PASSWORD_CHANGED(200, "Password has been changed.", HttpStatus.OK),

    // ===== Post =====
    CREATE_POST_SUCCESS(201, "Post created successfully.", HttpStatus.CREATED),
    CREATE_POST_FAILED(216, "Failed to create post.", HttpStatus.BAD_REQUEST),
    UPDATE_POST_SUCCESS(217, "Post updated successfully.", HttpStatus.OK),
    DELETE_POST_SUCCESS(213, "Post deleted successfully.", HttpStatus.OK),
    POST_NOT_FOUND(209, "Post not found.", HttpStatus.NOT_FOUND),
    FEED_EMPTY(214, "Feed is empty.", HttpStatus.OK),
    INTEREST_NOT_FOUND(215, "User interest data not found.", HttpStatus.NOT_FOUND),
    HASHTAG_NOT_FOUND(318, "Hashtag not found.", HttpStatus.NOT_FOUND),
    HASHTAG_ALREADY_EXISTS(319, "Hashtag already exists.", HttpStatus.CONFLICT),

    // ===== Comment =====
    CREATE_COMMENT_SUCCESS(205, "Comment created successfully.", HttpStatus.CREATED),
    CREATE_COMMENT_FAILED(206, "Failed to create comment.", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(212, "Comment not found.", HttpStatus.NOT_FOUND),
    REPLY_COMMENT_NOT_FOUND(306, "Reply comment not found.", HttpStatus.NOT_FOUND),

    // ===== Media / File =====
    FILE_NOT_FOUND(202, "File not found.", HttpStatus.NOT_FOUND),
    UPLOAD_FILE_SUCCESS(203, "File uploaded successfully.", HttpStatus.OK),
    UPLOAD_FILE_FAILED(204, "Failed to upload file.", HttpStatus.BAD_REQUEST),
    UPLOAD_MEDIA_FAILED(218, "Failed to upload media.", HttpStatus.BAD_REQUEST),
    UPLOAD_AVATAR_FAILED(312, "Failed to upload avatar.", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(308, "File size exceeds the allowed limit.", HttpStatus.PAYLOAD_TOO_LARGE),

    // ===== Chat =====
    CONVERSATION_NOT_FOUND(313, "Conversation not found.", HttpStatus.NOT_FOUND),
    NOT_CONVERSATION_MEMBER(314, "You are not a member of this conversation.", HttpStatus.FORBIDDEN),
    MESSAGE_SEND_FAILED(315, "Failed to send message.", HttpStatus.BAD_REQUEST),
    AI_SERVICE_FAILED(316, "AI service failed.", HttpStatus.BAD_GATEWAY),

    // ===== Notification / FAQ / Complaint / Term of Service =====
    SEND_MAIL_FAILED(500, "Failed to send mail.", HttpStatus.INTERNAL_SERVER_ERROR),
    TERM_OF_SERVICE_NOT_FOUND(305, "Term of service not found.", HttpStatus.NOT_FOUND),
    COMPLAINT_NOT_FOUND(307, "Complaint not found.", HttpStatus.NOT_FOUND),
    FAQ_NOT_FOUND(317, "FAQ not found.", HttpStatus.NOT_FOUND),

    // ===== Finance (category / product / transaction) =====
    FIN_CATEGORY_NOT_FOUND(320, "Category not found.", HttpStatus.NOT_FOUND),
    FIN_CATEGORY_NAME_ALREADY_EXISTS(321, "Category name already exists.", HttpStatus.CONFLICT),
    FIN_PRODUCT_NOT_FOUND(323, "Product not found.", HttpStatus.NOT_FOUND),
    FIN_PRODUCT_NAME_ALREADY_EXISTS(324, "Product name already exists in this category.", HttpStatus.CONFLICT),
    FIN_TRANSACTION_NOT_FOUND(326, "Transaction not found.", HttpStatus.NOT_FOUND),
    FIN_AMOUNT_CANNOT_BE_NEGATIVE(327, "Amount cannot be negative.", HttpStatus.BAD_REQUEST),
    FIN_WALLET_NOT_FOUND(300, "Wallets not found", HttpStatus.BAD_REQUEST)
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
