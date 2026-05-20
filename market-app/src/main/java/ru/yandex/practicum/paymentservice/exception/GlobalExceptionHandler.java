package ru.yandex.practicum.paymentservice.exception;

import java.util.Locale;

import jakarta.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MarketException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMarketException(MarketException ex, Locale locale, Model model) {
        log.warn("Business exception occurred. errorCode={}", ex.getErrorCode());
        String message = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, locale);
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler({ItemNotFoundException.class, OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(MarketException ex, Locale locale, Model model) {

        log.warn("Not found exception occurred. errorCode={}", ex.getErrorCode());

        String message = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, locale);

        model.addAttribute("message", message);

        return "error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException ex, Locale locale, Model model) {

        log.warn("Validation exception occurred. message={}", ex.getMessage());
        String message = messageSource.getMessage(ErrorCode.INVALID_PAGE_SIZE.getMessageKey(), null, locale);
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Locale locale, Model model) {
        log.error("Unexpected exception occurred", ex);
        String message = messageSource.getMessage(ErrorCode.INTERNAL_ERROR.getMessageKey(), null, locale);
        model.addAttribute("message", message);
        return "error";
    }
}
