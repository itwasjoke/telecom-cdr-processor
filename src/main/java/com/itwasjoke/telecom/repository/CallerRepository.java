package com.itwasjoke.telecom.repository;

import com.itwasjoke.telecom.entity.Caller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallerRepository extends JpaRepository<Caller, String> {
}
