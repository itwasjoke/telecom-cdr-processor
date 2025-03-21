package com.itwasjoke.telecom.controller;

import com.itwasjoke.telecom.dto.TotalTime;
import com.itwasjoke.telecom.dto.UDR;
import com.itwasjoke.telecom.service.UdrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class UdrControllerTest {
    @Mock
    private UdrService udrService;

    @InjectMocks
    private UdrController udrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUDRForCaller() {
        String number = "12345678901";
        Integer month = 5;
        UDR udr = new UDR(number, new TotalTime("01:00:00"), new TotalTime("00:30:00"));

        when(udrService.getUdrFromCaller(number, month)).thenReturn(udr);

        UDR result = udrController.getUDRForCaller(number, month);

        assertNotNull(result);
        assertEquals(number, result.msisdn());
    }

    @Test
    void testGetUDRForCaller_NoMonth() {
        String number = "12345678901";
        UDR udr = new UDR(number, new TotalTime("01:00:00"), new TotalTime("00:30:00"));

        when(udrService.getUdrFromCaller(number, null)).thenReturn(udr);

        UDR result = udrController.getUDRForCaller(number, null);

        assertNotNull(result);
        assertEquals(number, result.msisdn());
    }

    @Test
    void testGetUDRForCaller_List() {
        Integer month = 5;
        List<UDR> udrs = new ArrayList<>();
        udrs.add(new UDR("12345678901", new TotalTime("01:00:00"), new TotalTime("00:30:00")));
        udrs.add(new UDR("12345678902", new TotalTime("02:00:00"), new TotalTime("01:30:00")));

        when(udrService.getUdrsFromCaller(month)).thenReturn(udrs);

        List<UDR> result = udrController.getUDRForCaller(month);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUDRForCaller_List_NoMonth() {
        List<UDR> udrs = new ArrayList<>();
        udrs.add(new UDR("12345678901", new TotalTime("01:00:00"), new TotalTime("00:30:00")));
        udrs.add(new UDR("12345678902", new TotalTime("02:00:00"), new TotalTime("01:30:00")));

        when(udrService.getUdrsFromCaller(null)).thenReturn(udrs);

        List<UDR> result = udrController.getUDRForCaller(null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
