package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.Caller;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CdrService {
    void generateCDR();
    Long getDurationOutgoingCalls(
            Caller caller,
            LocalDateTime date1,
            LocalDateTime date2
    );
    Long getDurationIncomingCalls(
            Caller caller,
            LocalDateTime date1,
            LocalDateTime date2
    );
    UUID generateCdrReport(
            String number,
            LocalDateTime dateStart,
            LocalDateTime dateEnd
    );
}
