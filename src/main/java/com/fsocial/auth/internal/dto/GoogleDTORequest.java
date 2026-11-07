package com.fsocial.auth.internal.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleDTORequest(
        @NotBlank(message = "code is required")
        String code
) {
}
