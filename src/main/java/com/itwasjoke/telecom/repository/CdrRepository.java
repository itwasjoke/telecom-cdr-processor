package com.itwasjoke.telecom.repository;

import com.itwasjoke.telecom.entity.CDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdrRepository extends JpaRepository<CDR, Long> {
}
