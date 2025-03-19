package com.itwasjoke.telecom;

import com.itwasjoke.telecom.service.CdrService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CdrGeneratorRunner implements CommandLineRunner {

    private final CdrService cdrService;

    public CdrGeneratorRunner(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    @Override
    public void run(String... args) {
        cdrService.generateCDR();
    }
}
