package com.fsocial.shared.exception;

import org.springframework.http.HttpStatusCode;

public interface CodeEnum {
    int getCode();
    String getMessage();
    HttpStatusCode getHttpStatusCode();
}
