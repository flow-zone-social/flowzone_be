package com.fsocial.complaint.internal.service;

import com.fsocial.complaint.internal.dto.ComplaintDTO;
import com.fsocial.complaint.internal.dto.ComplaintDTOResponse;
import com.fsocial.complaint.internal.dto.ComplaintStatisticsDTO;
import com.fsocial.complaint.internal.dto.ComplaintStatisticsLongDayDTO;

import java.util.List;

public interface ComplaintService {
    ComplaintDTOResponse addComplaint(ComplaintDTO complaint, String userId);
    ComplaintDTO readComplaint(String complaintId);

    // Methods from timelineService
    List<ComplaintDTOResponse> getComplaints();
    ComplaintDTOResponse getComplaintById(String complaintId);
    List<ComplaintStatisticsDTO> countStatisticsComplainToday(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    List<ComplaintStatisticsLongDayDTO> countStatisticsComplainLongDay(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}
