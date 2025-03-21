package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.CDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.repository.CdrRepository;
import com.itwasjoke.telecom.service.impl.CdrServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CdrServiceImplTest {
    @Mock
    private CdrRepository cdrRepository;

    @Mock
    private CallerService callerService;

    @InjectMocks
    private CdrServiceImpl cdrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCDR() {
        List<Caller> callers = new ArrayList<>();
        Caller caller1 = new Caller();
        caller1.setMsisdn("12345678901");
        Caller caller2 = new Caller();
        caller2.setMsisdn("12345678902");
        callers.add(caller1);
        callers.add(caller2);

        when(callerService.generateCallers()).thenReturn(callers);

        cdrService.generateCDR();

        verify(cdrRepository, atLeastOnce()).save(any(CDR.class));
    }

    @Test
    void testGetDurationOutgoingCalls() {
        Caller caller = new Caller();
        caller.setMsisdn("12345678901");
        LocalDateTime date1 = LocalDateTime.now();
        LocalDateTime date2 = date1.plusDays(1);

        when(cdrRepository.findCDRByDatesOutgoing(
                        caller,
                        date1,
                        date2
        )).thenReturn(3600000000000L);

        Long duration
                = cdrService.getDurationOutgoingCalls(
                        caller, date1, date2
        );

        assertEquals(3600000000000L, duration);
    }

    @Test
    void testGetDurationIncomingCalls() {
        Caller caller = new Caller();
        caller.setMsisdn("12345678901");
        LocalDateTime date1 = LocalDateTime.now();
        LocalDateTime date2 = date1.plusDays(1);

        when(cdrRepository.findCDRByDatesIncoming(
                caller,
                date1,
                date2
        )).thenReturn(  1800000000000L);

        Long duration =
                cdrService.getDurationIncomingCalls(
                        caller,
                        date1,
                        date2
                );

        assertEquals(1800000000000L, duration);
    }
}
