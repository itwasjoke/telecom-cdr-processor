package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.dto.UDR;
import com.itwasjoke.telecom.entity.Caller;

import java.util.List;

public interface UdrService {
    UDR getUdrFromCaller(String number, Integer month);
    UDR getUdrWithCaller(Caller caller, Integer month);
    List<UDR> getUdrsFromCaller(Integer month);
}
