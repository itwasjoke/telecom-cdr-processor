package com.itwasjoke.telecom.repository;

import com.itwasjoke.telecom.entity.CDR;
import com.itwasjoke.telecom.entity.Caller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdrRepository extends JpaRepository<CDR, Long> {

    @Query("select sum(c.endTime - c.startTime) from CDR as c where c.callerNumber = :caller and (c.startTime between :date1 and :date2)")
    Long findCDRByDatesOutgoing(
            @Param("caller")Caller caller,
            @Param("date1") LocalDateTime date1,
            @Param("date2") LocalDateTime date2
    );

    @Query("select sum(c.endTime - c.startTime) from CDR as c where c.receiverNumber = :caller and (c.startTime between :date1 and :date2)")
    Long findCDRByDatesIncoming(
            @Param("caller")Caller caller,
            @Param("date1") LocalDateTime date1,
            @Param("date2") LocalDateTime date2
    );

    @Query("select c from CDR as c where (c.receiverNumber = :receiverNumber OR c.callerNumber = :caller) AND (c.startTime between :date1 AND :date2)")
    List<CDR> findAllForReport(
            @Param("caller") Caller caller,
            @Param("receiverNumber") Caller receiverNumber,
            @Param("date1") LocalDateTime date1,
            @Param("date2") LocalDateTime date2
    );

}
