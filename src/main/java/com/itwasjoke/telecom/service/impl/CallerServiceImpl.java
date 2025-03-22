package com.itwasjoke.telecom.service.impl;

import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.NoCallerFoundException;
import com.itwasjoke.telecom.repository.CallerRepository;
import com.itwasjoke.telecom.service.CallerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Сервис для работы с абонентами
 * Отвечает за генерацию абонентов и предоставление информации о них
 */
@Service
public class CallerServiceImpl implements CallerService {
    private final CallerRepository callerRepository;

    public CallerServiceImpl(CallerRepository callerRepository) {
        this.callerRepository = callerRepository;
    }

    /**
     * Создание базы 30 номеров абонентов
     */
    @Override
    public List<Caller> generateCallers() {
        List<Caller> callers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            // делаем проверку на существование номера
            String msisdn;
            do {
               msisdn = getMsisdn();
            } while (callerRepository.existsById(msisdn));

            // сохраняем абонента
            Caller caller = new Caller();
            caller.setMsisdn(msisdn);
            callers.add(callerRepository.save(caller));
        }
        return callers;
    }

    /**
     * Получение абонента
     * @param number номер абонента
     */
    @Override
    public Caller getCaller(String number) {
        Optional<Caller> caller =
                callerRepository.findCallerByMsisdn(number);
        if (caller.isPresent()) {
            return caller.get();
        } else {
            throw new NoCallerFoundException(
                    "No caller found with number: " + number
            );
        }
    }

    /**
     * Получение списка всех абонентов
     */
    @Override
    public List<Caller> getCallers() {
        return callerRepository.findAll();
    }

    /**
     * Генерация номера
     * @return строка из 11 чисел
     */
    public String getMsisdn() {
        return new Random()
                .ints(11, 0, 10)
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                ).toString();
    }
}
