package com.fsocial.termofservice.internal.service;

import com.fsocial.termofservice.api.TermOfServiceApi;
import com.fsocial.termofservice.api.TermSummary;
import com.fsocial.termofservice.internal.dto.TermOfServiceDTO;
import com.fsocial.termofservice.internal.entity.TermOfServices;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.termofservice.internal.mapper.TermOfServiceMapper;
import com.fsocial.termofservice.internal.repository.TermRepository;
import com.fsocial.termofservice.internal.service.TermOfServicesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor()
public class TermOfServicesServiceImpl implements TermOfServicesService, TermOfServiceApi {

    TermRepository termRepository;
    TermOfServiceMapper termOfServiceMapper;

    @Override
    public List<TermOfServiceDTO> getTermOfServices() {

        List<TermOfServices> res = termRepository.findAll();

        return termOfServiceMapper.toListDTO(res);
    }

    @Override
    public TermOfServiceDTO addTermOfService(TermOfServiceDTO termOfService) {
        return termOfServiceMapper.toDTO(termRepository.save(termOfServiceMapper.toEntity(termOfService)));
    }

    @Override
    public TermOfServiceDTO updateTermOfService(TermOfServiceDTO termOfService) {

        TermOfServices findById = termRepository.findById(termOfService.getId()).orElseThrow(() -> new AppException("Không tìm thấy chính sách", StatusCode.TERM_OF_SERVICE_NOT_FOUND));

        findById.setName(termOfService.getName());

        return termOfServiceMapper.toDTO(termRepository.save(findById));
    }

    @Override
    public String deleteTermOfService(String termOfServiceId) {
         termRepository.deleteById(termOfServiceId);
        return "Xóa chính sách thành công";
    }

    @Override
    public TermSummary findSummaryById(String id) {
        TermOfServices term = termRepository.findById(id).orElseThrow(
                () -> new AppException("Không tìm thấy chính sách", StatusCode.TERM_OF_SERVICE_NOT_FOUND));
        return TermSummary.builder()
                .id(term.getId())
                .name(term.getName())
                .build();
    }

}
