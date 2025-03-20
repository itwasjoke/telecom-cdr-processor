package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.Caller;

import java.time.LocalDateTime;

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
}
