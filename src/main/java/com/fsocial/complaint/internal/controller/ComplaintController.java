package com.fsocial.complaint.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.complaint.internal.dto.ComplaintDTO;
import com.fsocial.complaint.internal.dto.ComplaintDTOResponse;
import com.fsocial.complaint.internal.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/complaint")
@Slf4j
public class ComplaintController {

    ComplaintService complaintService;

    @PostMapping
    public ApiResponse<ComplaintDTOResponse> addComplaint(@RequestBody @Valid ComplaintDTO complaint, @AuthenticationPrincipal Jwt jwt) {
        ComplaintDTOResponse complaintDTO = complaintService.addComplaint(complaint, jwt.getSubject());
        return ApiResponse.<ComplaintDTOResponse>builder()
                .data(complaintDTO)
                .message("Report submitted successfully")
                .dateTime(LocalDateTime.now())
                .build();
    }

    @PutMapping("/reading")
    public ApiResponse<ComplaintDTO> updateComplaint(@RequestParam("complaint_id") String complaintId) {
        return ApiResponse.<ComplaintDTO>builder()
                .data(complaintService.readComplaint(complaintId))
                .message("Report read status updated successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<ComplaintDTOResponse>> getComplaints() {
        return ApiResponse.<List<ComplaintDTOResponse>>builder()
                .data(complaintService.getComplaints())
                .message("Reports retrieved successfully")
                .dateTime(LocalDateTime.now())
                .build();
    }

}
