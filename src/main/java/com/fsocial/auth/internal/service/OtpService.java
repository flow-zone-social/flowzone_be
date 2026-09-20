package com.fsocial.auth.internal.service;

import com.fsocial.auth.internal.dto.EmailRequest;
import com.fsocial.auth.internal.dto.OtpRequest;

public interface OtpService {
    void sendOtp(String email, String keyPrefix);
    void validateOtp(String email, String otp, String keyPrefix);
    void deleteOtp(String email, String keyPrefix);
    void validEmailBeforePersist(String email);
    void sortTypeForSendOtp(EmailRequest request);
    void sortTypeForVerifyOtp(OtpRequest request);
}
