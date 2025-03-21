package com.itwasjoke.telecom;

import com.itwasjoke.telecom.service.CdrService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner, отвечающий за генерацию отчетов
 * при запуске микросервиса
 */
@Component
public class CdrGeneratorRunner implements CommandLineRunner {

    private final CdrService cdrService;

    public CdrGeneratorRunner(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    /**
     * Генерация записей
     */
    @Override
    public void run(String... args) {
        cdrService.generateCDR();
    }
}
