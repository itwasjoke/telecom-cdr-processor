package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.CDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.IncorrectDateFormatException;
import com.itwasjoke.telecom.repository.CdrRepository;
import com.itwasjoke.telecom.service.impl.CdrServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CdrServiceImplTest {
    @Mock
    private CdrRepository cdrRepository;

    @Mock
    private CallerService callerService;

    @InjectMocks
    private CdrServiceImpl cdrService;


    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cdrService = new CdrServiceImpl(cdrRepository, callerService) {
            @Override
            public Path openFolder() {
                return tempDir;
            }
        };
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

    @Test
    void testFormatCdr_IncomingCall() {
        // Arrange
        Caller caller = new Caller();
        caller.setMsisdn("12345678901");

        Caller receiver = new Caller();
        receiver.setMsisdn("98765432109");

        CDR cdr = new CDR();
        cdr.setCallerNumber(caller);
        cdr.setReceiverNumber(receiver);
        cdr.setStartTime(LocalDateTime.of(
                2023,
                1,
                1,
                10,
                0
        ));
        cdr.setEndTime(LocalDateTime.of(
                2023,
                1,
                1,
                10,
                30
        ));

        String result = cdrService.formatCdr(cdr, "98765432109");

        String expected = "02, 12345678901, 98765432109, 2023-01-01T10:00:00, 2023-01-01T10:30:00";
        assertEquals(expected, result);
    }

    @Test
    void testFormatCdr_OutgoingCall() {
        Caller caller = new Caller();
        caller.setMsisdn("12345678901");

        Caller receiver = new Caller();
        receiver.setMsisdn("98765432109");

        CDR cdr = new CDR();
        cdr.setCallerNumber(caller);
        cdr.setReceiverNumber(receiver);
        cdr.setStartTime(LocalDateTime.of(
                2023,
                1,
                1,
                10,
                0
        ));
        cdr.setEndTime(LocalDateTime.of(
                2023,
                1,
                1, 10,
                30
        ));

        String result = cdrService.formatCdr(cdr, "12345678901");

        String expected = "01, 12345678901, 98765432109, 2023-01-01T10:00:00, 2023-01-01T10:30:00";
        assertEquals(expected, result);
    }

    @Test
    void testGenerateCdrReport_IncorrectDateRange() {
        String number = "12345678901";
        LocalDateTime dateStart = LocalDateTime.of(
                2023,
                2,
                1, 0,
                0
        );
        LocalDateTime dateEnd = LocalDateTime.of(
                2023,
                1,
                31, 23,
                59
        );

        assertThrows(IncorrectDateFormatException.class, () ->
                cdrService.generateCdrReport(number, dateStart, dateEnd)
        );
    }

    @Test
    void testGenerateCdrReport_Success() {
        String number = "12345678901";
        LocalDateTime dateStart = LocalDateTime.of(
                2023,
                1,
                1,
                0,
                0
        );
        LocalDateTime dateEnd = LocalDateTime.of(
                2023,
                1,
                31,
                23,
                59
        );

        Caller caller = new Caller();
        caller.setMsisdn(number);

        CDR cdr = new CDR();
        cdr.setCallerNumber(caller);
        cdr.setReceiverNumber(caller);
        cdr.setStartTime(dateStart.plusHours(1));
        cdr.setEndTime(dateStart.plusHours(2));

        when(callerService.getCaller(number)).thenReturn(caller);
        when(cdrRepository
                .findAllForReport(
                        caller,
                        caller,
                        dateStart,
                        dateEnd
                ))
                .thenReturn(List.of(cdr));

        UUID resultUuid = cdrService
                .generateCdrReport(
                        number,
                        dateStart,
                        dateEnd
                );

        assertNotNull(resultUuid);
        verify(cdrRepository, times(1))
                .findAllForReport(
                        caller,
                        caller,
                        dateStart,
                        dateEnd
                );

        Path expectedFilePath =
                tempDir.resolve(
                        number + "_" + resultUuid + ".txt"
                );
        assertTrue(Files.exists(expectedFilePath));

        try {
            List<String> lines = Files.readAllLines(expectedFilePath);
            assertEquals(1, lines.size());
            String expectedLine = "01, 12345678901, 12345678901, " +
                    cdr.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ", " +
                    cdr.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            assertEquals(expectedLine, lines.get(0));
        } catch (IOException e) {
            fail("Failed to read file: " + e.getMessage());
        }
    }
}
