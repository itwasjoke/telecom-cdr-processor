package com.itwasjoke.telecom.controller;

import com.itwasjoke.telecom.dto.UDR;
import com.itwasjoke.telecom.service.UdrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/udr")
public class UdrController {

    private final UdrService udrService;

    public UdrController(UdrService udrService) {
        this.udrService = udrService;
    }

    @GetMapping("/caller")
    UDR getUDRForCaller(
            @RequestParam String number,
            @RequestParam(required = false) Integer month
            ) {
        return udrService.getUdrFromCaller(
                number,
                month
        );
    }

    @GetMapping()
    List<UDR> getUDRForCaller(
            @RequestParam(required = false) Integer month
    ){
        return udrService.getUdrsFromCaller(month);
    }
}
