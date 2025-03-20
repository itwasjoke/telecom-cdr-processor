package com.itwasjoke.telecom.repository;

import com.itwasjoke.telecom.entity.Caller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CallerRepository extends JpaRepository<Caller, String> {
    Optional<Caller> findCallerByMsisdn(String msisdn);
}
