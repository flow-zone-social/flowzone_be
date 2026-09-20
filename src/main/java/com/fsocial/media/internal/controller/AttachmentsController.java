package com.fsocial.media.internal.controller;

import com.fsocial.shared.response.ApiDataCursor;
import com.fsocial.shared.response.ApiResponse;
import com.fsocial.media.internal.dto.AttachementProfileDTO;
import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.internal.dto.AttachmentsRequest;
import com.fsocial.media.internal.service.AttachmentsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/attachment")
public class AttachmentsController {

    AttachmentsService attachmentsService;

    @PostMapping
    public ApiResponse<ApiDataCursor<AttachementProfileDTO>> getAttachment(@RequestBody AttachmentsRequest pageRequest,
                                                                           @AuthenticationPrincipal Jwt jwt) {
        ApiDataCursor<AttachementProfileDTO> response =
                attachmentsService.getAttachmentByOwnerId(pageRequest, jwt.getSubject());
        return ApiResponse.<ApiDataCursor<AttachementProfileDTO>>builder()
                .data(response)
                .message("Get attachments successfully!")
                .build();
    }
}
