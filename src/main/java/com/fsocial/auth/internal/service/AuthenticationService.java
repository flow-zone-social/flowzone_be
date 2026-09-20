package com.fsocial.auth.internal.service;

import com.fsocial.auth.internal.dto.GoogleDTORequest;
import com.fsocial.auth.internal.dto.AccountLoginRequest;
import com.fsocial.auth.internal.dto.AuthenticationResponse;
import com.fsocial.auth.internal.dto.IntrospectResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse login(AccountLoginRequest request, String userAgent, HttpServletRequest httpRequest);
    IntrospectResponse introspect(String token);
    AuthenticationResponse loginWithGoogle(GoogleDTORequest request, String userAgent, HttpServletRequest httpRequest);
}
