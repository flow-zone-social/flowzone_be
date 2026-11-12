package com.fsocial.auth.internal.repository;

import com.fsocial.auth.internal.entity.AuthProviderCredential;
import com.fsocial.auth.api.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProviderCredential, String> {
    Optional<AuthProviderCredential> findByUser_IdAndProvider(String accountId, AuthProvider provider);
    Optional<AuthProviderCredential> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
