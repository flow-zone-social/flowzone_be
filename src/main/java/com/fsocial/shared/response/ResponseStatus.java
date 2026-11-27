package com.fsocial.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS(200,"Operation successful."),
    ERROR(404, "Operation failed.")
    ;

    private final int CODE;
    private final String message;
}
