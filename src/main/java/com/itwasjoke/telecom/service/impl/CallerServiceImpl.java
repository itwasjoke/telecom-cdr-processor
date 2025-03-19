package com.itwasjoke.telecom.service.impl;

import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.repository.CallerRepository;
import com.itwasjoke.telecom.service.CallerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CallerServiceImpl implements CallerService {
    private final CallerRepository callerRepository;

    public CallerServiceImpl(CallerRepository callerRepository) {
        this.callerRepository = callerRepository;
    }

    @Override
    public List<Caller> getCallers() {
        return callerRepository.findAll();
    }

    @Override
    public List<Caller> generateCallers() {
        List<Caller> callers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String msisdn;
            do {
               msisdn = getMsisdn();
            } while (callerRepository.existsById(msisdn));
            Caller caller = new Caller();
            caller.setMsisdn(msisdn);
            callers.add(callerRepository.save(caller));
        }
        return callers;
    }

    /**
     * Генерация номера телефона
     * @return строка из 11 чисел
     */
    private String getMsisdn() {
        return new Random()
                .ints(11, 0, 10)
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                ).toString();
    }
}
