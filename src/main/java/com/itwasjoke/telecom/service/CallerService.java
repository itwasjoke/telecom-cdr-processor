package com.itwasjoke.telecom.service;

import com.itwasjoke.telecom.entity.Caller;

import java.util.List;

public interface CallerService {
    List<Caller> generateCallers();
    Caller getCaller(String number);
    List<Caller> getCallers();
}
