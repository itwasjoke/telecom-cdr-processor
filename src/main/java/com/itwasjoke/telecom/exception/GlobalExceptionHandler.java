package com.itwasjoke.telecom.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработка ошибок и их вывод во время запроса
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> handleException(
            Exception e,
            ExceptionMessage body,
            HttpStatus status,
            WebRequest request
    ) {
        log.error("Error with this message: {}", e.getMessage());
        return handleExceptionInternal(
                e,
                body,
                new HttpHeaders(),
                status,
                request
        );
    }

    @ExceptionHandler(value = {
            IncorrectMonthException.class,
            IncorrectDateFormatException.class
    })
    public ResponseEntity<Object> handleIncorrectMonthException(
            Exception e,
            WebRequest request
    ) {
        return handleException(
                e,
                new ExceptionMessage(400, "Неправильный формат даты"),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(value = {NoCallerFoundException.class})
    public ResponseEntity<Object> handleNoCallerFoundException(
            Exception e,
            WebRequest request
    ){
        return handleException(
                e,
                new ExceptionMessage(404, "Номер абонента не найден"),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(value = {NoFolderFoundException.class})
    public ResponseEntity<Object> handleFolderNotFoundException(
            Exception e,
            WebRequest request
    ){
        return handleException(
                e,
                new ExceptionMessage(404, "Нет папки для сохранения отчета"),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(value = {WritingToFileException.class})
    public ResponseEntity<Object> handleWritingToFileException(
            Exception e,
            WebRequest request
    ){
        return handleException(
                e,
                new ExceptionMessage(500, "Ошибка при записи в файл"),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}
