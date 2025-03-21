package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.dto.UDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.IncorrectMonthException;
import com.itwasjoke.telecom.service.impl.UdrServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UdrServiceImplTest {
    @Mock
    private CallerService callerService;

    @Mock
    private CdrService cdrService;

    @InjectMocks
    private UdrServiceImpl udrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUdrFromCaller_ValidMonth() {
        String number = "12345678901";
        Integer month = 5;
        Caller caller = new Caller();
        caller.setMsisdn(number);

        when(callerService.getCaller(number)).thenReturn(caller);
        when(cdrService.getDurationOutgoingCalls(
                any(),
                any(),
                any())
        ).thenReturn(3600000000000L);
        when(cdrService.getDurationIncomingCalls(
                any(),
                any(),
                any())
        ).thenReturn(1800000000000L);

        UDR udr = udrService.getUdrFromCaller(number, month);

        assertNotNull(udr);
        assertEquals(number, udr.msisdn());
        assertEquals("01:00:00", udr.outcomingCall().totalTime());
        assertEquals("00:30:00", udr.incomingCall().totalTime());
    }

    @Test
    void testGetUdrFromCaller_InvalidMonth() {
        String number = "12345678901";
        Integer month = 13;

        assertThrows(
                IncorrectMonthException.class, ()
                        -> udrService.getUdrFromCaller(number, month)
        );
    }

    @Test
    void testGetUdrsFromCaller_ValidMonth() {
        Integer month = 5;
        Caller caller1 = new Caller();
        caller1.setMsisdn("12345678901");
        Caller caller2 = new Caller();
        caller2.setMsisdn("12345678902");

        List<Caller> callers = new ArrayList<>();
        callers.add(caller1);
        callers.add(caller2);

        when(callerService.getCallers()).thenReturn(callers);
        when(cdrService.getDurationOutgoingCalls(
                any(),
                any(),
                any())
        ).thenReturn(3600000000000L);
        when(cdrService.getDurationIncomingCalls(
                any(),
                any(),
                any())
        ).thenReturn(   1800000000000L);

        List<UDR> udrs = udrService.getUdrsFromCaller(month);

        assertNotNull(udrs);
        assertEquals(2, udrs.size());
    }

    @Test
    void testGetUdrsFromCaller_InvalidMonth() {
        Integer month = 13;

        assertThrows(
                IncorrectMonthException.class, ()
                        -> udrService.getUdrsFromCaller(month)
        );
    }

    @Test
    void testGetDateStart_WithMonth() {
        Integer month = 5;
        LocalDateTime startDate = udrService.getDateStart(month);

        assertEquals(Year.now().getValue(), startDate.getYear());
        assertEquals(month, startDate.getMonthValue());
        assertEquals(1, startDate.getDayOfMonth());
    }

    @Test
    void testGetDateStart_WithoutMonth() {
        LocalDateTime startDate = udrService.getDateStart(null);

        assertEquals(Year.now().getValue(), startDate.getYear());
        assertEquals(Month.JANUARY, startDate.getMonth());
        assertEquals(1, startDate.getDayOfMonth());
    }

    @Test
    void testGetDateEnd_WithMonth() {
        Integer month = 5;
        LocalDateTime endDate = udrService.getDateEnd(month);

        assertEquals(Year.now().getValue(), endDate.getYear());
        assertEquals(month, endDate.getMonthValue());
        assertEquals(
                YearMonth.of(
                        Year.now().getValue(), month
                )
                        .atEndOfMonth()
                        .getDayOfMonth(),
                endDate.getDayOfMonth()
        );
    }

    @Test
    void testGetDateEnd_WithoutMonth() {
        LocalDateTime endDate = udrService.getDateEnd(null);

        assertEquals(Year.now().getValue(), endDate.getYear());
        assertEquals(Month.DECEMBER, endDate.getMonth());
        assertEquals(31, endDate.getDayOfMonth());
    }

    @Test
    void testGetTimeText() {
        assertEquals(
                "01:00:00",
                udrService.getTimeText(3600000000000L)
        );
        assertEquals(
                "00:00:00",
                udrService.getTimeText(null)
        );
    }
}
