package com.itwasjoke.telecom;

import com.itwasjoke.telecom.service.CdrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CdrGeneratorRunnerTest {
    @Mock
    private CdrService cdrService;

    @InjectMocks
    private CdrGeneratorRunner cdrGeneratorRunner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRun() throws Exception {
        cdrGeneratorRunner.run();

        verify(cdrService, times(1))
                .generateCDR();
    }
}
