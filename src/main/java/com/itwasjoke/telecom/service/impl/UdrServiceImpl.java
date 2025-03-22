package com.itwasjoke.telecom.service.impl;

import com.itwasjoke.telecom.dto.TotalTime;
import com.itwasjoke.telecom.dto.UDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.IncorrectMonthException;
import com.itwasjoke.telecom.service.CallerService;
import com.itwasjoke.telecom.service.CdrService;
import com.itwasjoke.telecom.service.UdrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для генерации UDR отчетов на основе CDR данных
 * UDR отчет содержит агрегированную информацию
 * о входящих и исходящих звонках абонента за указанный месяц
 */
@Service
public class UdrServiceImpl implements UdrService {
    private final CallerService callerService;
    private final CdrService cdrService;
    private final Logger logger = LoggerFactory.getLogger(UdrServiceImpl.class);

    public UdrServiceImpl(
            CallerService callerService,
            CdrService cdrService
    ) {
        this.callerService = callerService;
        this.cdrService = cdrService;
    }

    /**
     * Получение UDR отчета
     * @param number номер абонента
     * @param month запрашиваемый месяц
     */
    @Override
    public UDR getUdrFromCaller(String number, Integer month) {
        if (month != null) {
            if (month < 1 || month > 12) {
                throw new IncorrectMonthException("Incorrect month: " + month);
            }
        }
        Caller caller = callerService.getCaller(number);
        return getUdrWithCaller(caller, month);
    }

    /**
     * Получение отчета UDR
     * @param caller заранее найденный абонент
     * @param month запрашиваемый месяц
     * @return
     */
    @Override
    public UDR getUdrWithCaller(Caller caller, Integer month) {
        LocalDateTime startDate = getDateStart(month);
        LocalDateTime endDate = getDateEnd(month);

        Long secsOutgoing = cdrService.getDurationOutgoingCalls(
                caller,
                startDate,
                endDate
        );
        Long secsIncoming = cdrService.getDurationIncomingCalls(
                caller,
                startDate,
                endDate
        );
        String formattedTimeOutgoing = getTimeText(secsOutgoing);
        String formattedTimeIncoming = getTimeText(secsIncoming);

        return new UDR(
                caller.getMsisdn(),
                new TotalTime(formattedTimeIncoming),
                new TotalTime(formattedTimeOutgoing)
        );
    }

    /**
     * Получение UDR отчетов по всем абонентам
     * @param month запрашиваемый месяц
     */
    @Override
    public List<UDR> getUdrsFromCaller(Integer month) {
        if (month != null) {
            if (month < 1 || month > 12) {
                throw new IncorrectMonthException("Incorrect month: " + month);
            }
        }
        List<Caller> callers = callerService.getCallers();
        if (callers.isEmpty()) {
            return new ArrayList<>();
        }
        return callers
                .stream()
                .map(caller -> getUdrWithCaller(caller, month))
                .collect(Collectors.toList());
    }

    /**
     * Получение даты начала периода
     * @param month по месяцу
     */
    public LocalDateTime getDateStart(Integer month) {
        LocalDateTime startDate;

        if (month == null) {
            startDate = Year.now()
                    .atMonth(Month.JANUARY)
                    .atDay(1)
                    .atStartOfDay();
        } else {
            YearMonth yearMonth = YearMonth.of(
                    Year.now().getValue(), month
            );
            startDate = yearMonth
                    .atDay(1)
                    .atStartOfDay();
        }
        return startDate;
    }

    /**
     * Получение даты конца периода
     * @param month по месяцу
     */
    public LocalDateTime getDateEnd(Integer month) {
        LocalDateTime endDate;
        if (month == null) {
            endDate = Year.now()
                    .atMonth(Month.DECEMBER)
                    .atEndOfMonth()
                    .atTime(
                            23,
                            59,
                            59
                    );
        } else {
            YearMonth yearMonth = YearMonth.of(
                    Year.now().getValue(), month
            );
            endDate = yearMonth
                    .atEndOfMonth()
                    .atTime(
                            23,
                            59,
                            59
                    );
        }
        return endDate;
    }

    /**
     * Форматирование времени к текстовому формату
     * @param secs наносекунды
     * @return строка формата "hh:mm:ss"
     */
    public String getTimeText(Long secs){
        if (secs == null) {
            return "00:00:00";
        }
        Duration duration = Duration.ofNanos(secs);

        return String.format(
                "%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutes() % 60,
                duration.getSeconds() % 60
        );
    }
}
