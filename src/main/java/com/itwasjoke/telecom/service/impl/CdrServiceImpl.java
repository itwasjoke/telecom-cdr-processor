package com.itwasjoke.telecom.service.impl;

import com.itwasjoke.telecom.entity.CDR;
import com.itwasjoke.telecom.entity.Caller;
import com.itwasjoke.telecom.exception.IncorrectDateFormatException;
import com.itwasjoke.telecom.exception.NoFolderFoundException;
import com.itwasjoke.telecom.exception.WritingToFileException;
import com.itwasjoke.telecom.repository.CdrRepository;
import com.itwasjoke.telecom.service.CallerService;
import com.itwasjoke.telecom.service.CdrService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с CDR данными
 * Отвечает за генерацию CDR записей
 * и предоставление информации о длительности звонков
 */
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
     * Генерация записей CDR, начиная с 1 января текущего года
     */
    @Override
    @Transactional
    public void generateCDR() {
        // получаем всех абонентов
        Random random = new Random();
        List<Caller> callers = callerService.generateCallers();
        LocalDateTime startDate = Year.now()
                        .atMonth(Month.JANUARY)
                        .atDay(1)
                        .atStartOfDay();
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
     * Получение длительности исходящих звонков
     * @param caller абонент
     * @param date1 дата начала периода
     * @param date2 дата конца периода
     * @return наносекунды
     */
    @Override
    public Long getDurationOutgoingCalls(
            Caller caller,
            LocalDateTime date1,
            LocalDateTime date2
    ) {
        return cdrRepository.findCDRByDatesOutgoing(
                caller,
                date1,
                date2
        );
    }

    /**
     * Получение длительности входящих звонков
     * @param caller абонент
     * @param date1 дата начала периода
     * @param date2 дата конца периода
     * @return наносекунды
     */
    @Override
    public Long getDurationIncomingCalls(
            Caller caller,
            LocalDateTime date1,
            LocalDateTime date2
    ) {
        return cdrRepository.findCDRByDatesIncoming(
                caller,
                date1,
                date2
        );
    }

    /**
     * Генерация отчета по записям CDR
     * @param number номер абонента
     * @param dateStart дата начала периода
     * @param dateEnd дата конца периода
     * @return идентификатор отчета
     */
    @Override
    public UUID generateCdrReport(
            String number,
            LocalDateTime dateStart,
            LocalDateTime dateEnd
    ) {
        if (dateStart.isAfter(dateEnd)) {
            throw new IncorrectDateFormatException("Date start must be before date end");
        }
        UUID uuid = UUID.randomUUID();
        Caller caller = callerService.getCaller(number);
        List<CDR> cdrs = cdrRepository.findAllForReport(
                caller,
                caller,
                dateStart,
                dateEnd
        );
        saveCdrToFile(cdrs, number, uuid);
        return uuid;
    }

    /**
     * Создание записи CDR
     * @param caller абонент, инициирующий звонок
     * @param receiver абонент, принимающий звонок
     * @param startTime дата начала звонка
     */
    public void createCdr(
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

    /**
     * Сохранение отчета в файл
     * @param cdrs список записей
     * @param number номер текущего абонента
     * @param uuid идентификатор отчета
     */
    public void saveCdrToFile(List<CDR> cdrs, String number, UUID uuid) {
        Path path = openFolder();
        String fileName = number + "_" + uuid.toString() + ".txt";
        Path filePath = path.resolve(fileName);
        try (
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(filePath.toFile())
                )
        ) {
            List<String> lines = cdrs.stream()
                    .map(cdr -> formatCdr(cdr, number))
                    .toList();

            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new WritingToFileException("Error for write to file: " + e.getMessage());
        }
    }

    /**
     * Открытие папки или ее создание
     * @return путь к папке
     */
    public Path openFolder(){
        Path reportsDir = Paths.get("reports");

        if (!Files.exists(reportsDir)) {
            try {
                Files.createDirectories(reportsDir);
            } catch (IOException e) {
                throw new NoFolderFoundException("Cannot create folder: " + e.getMessage());
            }
        }
        return reportsDir;
    }

    /**
     * Форматирование записи CDR для отчета
     * @param cdr объект отчета
     * @param number номер абонента
     * @return строка записи
     */
    public String formatCdr(CDR cdr, String number) {
        String prefix = number
                .equals(
                        cdr.getCallerNumber().getMsisdn()
                ) ? "01" : "02";

        return prefix + ", " +
                cdr.getCallerNumber().getMsisdn() + ", " +
                cdr.getReceiverNumber().getMsisdn() + ", " +
                cdr.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ", " +
                cdr.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
