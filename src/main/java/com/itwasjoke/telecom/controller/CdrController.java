package com.itwasjoke.telecom.controller;

import com.itwasjoke.telecom.service.CdrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/cdr")
public class CdrController {
    private final CdrService cdrService;

    public CdrController(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    @GetMapping()
    public UUID generateCdrReport(
            @RequestParam String number,
            @RequestParam LocalDateTime dateStart,
            @RequestParam LocalDateTime dateEnd
    ){
        return cdrService.generateCdrReport(
                number,
                dateStart,
                dateEnd
        );
    }
}
