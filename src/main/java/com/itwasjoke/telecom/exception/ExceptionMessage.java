package com.itwasjoke.telecom.exception;

public record ExceptionMessage(
        Integer code,
        String message
) {
}
