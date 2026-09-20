package com.fsocial.termofservice.internal.service;

import com.fsocial.termofservice.internal.dto.TermOfServiceDTO;

import java.util.List;

public interface TermOfServicesService {
    TermOfServiceDTO addTermOfService(TermOfServiceDTO termOfServiceDTO);
    TermOfServiceDTO updateTermOfService(TermOfServiceDTO termOfService);
    String deleteTermOfService(String termOfServiceId);
    List<TermOfServiceDTO> getTermOfServices();
}
