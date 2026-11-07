package com.fsocial.complaint.internal.mapper;

import com.fsocial.complaint.internal.dto.ComplaintDTO;
import com.fsocial.complaint.internal.entity.Complaint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplantMapper {
    ComplaintDTO toComplaintDTO(Complaint complaint);
    Complaint toComplaint(ComplaintDTO complaintDTO);
    List<ComplaintDTO> toComplaintDTO(List<Complaint> complaints);
}
