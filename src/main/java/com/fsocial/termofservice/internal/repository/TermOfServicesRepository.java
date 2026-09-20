package com.fsocial.termofservice.internal.repository;

import com.fsocial.termofservice.internal.entity.TermOfServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermOfServicesRepository extends JpaRepository<TermOfServices, String> {
}
