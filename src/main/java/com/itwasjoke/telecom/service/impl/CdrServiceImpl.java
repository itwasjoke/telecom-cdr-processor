package com.itwasjoke.telecom.service.impl;

import com.itwasjoke.telecom.entity.CDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.repository.CdrRepository;
import com.itwasjoke.telecom.service.CallerService;
import com.itwasjoke.telecom.service.CdrService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CdrServiceImpl implements CdrService {
    private final CdrRepository cdrRepository;
    private final CallerService callerService;
    private final Logger logger = LoggerFactory.getLogger(CdrServiceImpl.class);

    public CdrServiceImpl(
            CdrRepository cdrRepository,
            CallerService callerService
    ) {
        this.cdrRepository = cdrRepository;
        this.callerService = callerService;
    }

    /**
     * Генерация записей CDR
     */
    @Override
    @Transactional
    public void generateCDR() {
        // получаем всех абонентов
        Random random = new Random();
        List<Caller> callers = callerService.generateCallers();
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        // добавляем до 10 записей на день
        for (int i = 0; i < 365; i++) {
            for (int j = 0; j < random.nextInt(5); j++) {

                // выбираем случайных абонентов
                int idCaller = random.nextInt(callers.size());
                Caller caller = callers.get(idCaller);
                int idReceiver;
                do {
                    idReceiver = random.nextInt(callers.size());
                } while (idReceiver == idCaller);
                Caller receiver = callers.get(idReceiver);

                // создание записи и добавление дня
                createCdr(caller, receiver, startDate);
            }
            startDate = startDate.plusDays(1);
        }
        logger.info("All CDR records are generated correctly");
    }

    /**
     * Сохранение записи CDR
     * @param caller звонящий
     * @param receiver принимающий
     * @param startTime дата старта дня
     */
    private void createCdr(
            Caller caller,
            Caller receiver,
            LocalDateTime startTime
    ) {
        Random random = new Random();
        CDR cdr = new CDR();
        cdr.setCallerNumber(caller);
        cdr.setReceiverNumber(receiver);
        cdr.setStartTime(
                startTime.plusMinutes(
                        random.nextInt(1400)
                )
        );
        cdr.setEndTime(
                cdr.getStartTime().plusMinutes(
                        random.nextInt(40)
                )
        );
        cdrRepository.save(cdr);
    }
}
