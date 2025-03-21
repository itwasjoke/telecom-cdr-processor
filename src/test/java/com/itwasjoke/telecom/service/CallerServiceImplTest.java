package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.NoCallerFoundException;
import com.itwasjoke.telecom.repository.CallerRepository;
import com.itwasjoke.telecom.service.impl.CallerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CallerServiceImplTest {
    @Mock
    private CallerRepository callerRepository;

    @InjectMocks
    private CallerServiceImpl callerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCallers() {
        when(callerRepository.existsById(anyString()))
                .thenReturn(false);
        when(callerRepository.save(any(Caller.class)))
                .thenReturn(new Caller());

        List<Caller> callers
                = callerService.generateCallers();

        assertNotNull(callers);
        assertEquals(30, callers.size());
    }

    @Test
    void testGetCaller() {
        String number = "12345678901";
        Caller caller = new Caller();
        caller.setMsisdn(number);

        when(callerRepository.findCallerByMsisdn(number))
                .thenReturn(Optional.of(caller));

        Caller result = callerService.getCaller(number);

        assertNotNull(result);
        assertEquals(number, result.getMsisdn());
    }

    @Test
    void testGetCaller_NotFound() {
        String number = "12345678901";

        when(callerRepository.findCallerByMsisdn(number))
                .thenReturn(Optional.empty());

        assertThrows(
                NoCallerFoundException.class, ()
                        -> callerService.getCaller(number)
        );
    }

    @Test
    void testGetCallers() {
        List<Caller> callers = new ArrayList<>();
        callers.add(new Caller());
        callers.add(new Caller());

        when(callerRepository.findAll()).thenReturn(callers);

        List<Caller> result = callerService.getCallers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
