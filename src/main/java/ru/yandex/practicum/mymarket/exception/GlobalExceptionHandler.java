package ru.yandex.practicum.mymarket.exception;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MarketException.class)
    public ResponseEntity<String> handleMarketException(
        MarketException ex,
        Locale locale
    ) {

        String message = messageSource.getMessage(
            ex.getErrorCode().getMessageKey(),
            null,
            locale
        );

        return ResponseEntity
            .badRequest()
            .body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(
        Exception ex,
        Locale locale
    ) {

        String message = messageSource.getMessage(
            ErrorCode.INTERNAL_ERROR.getMessageKey(),
            null,
            locale
        );

        return ResponseEntity
            .internalServerError()
            .body(message);
    }
}
