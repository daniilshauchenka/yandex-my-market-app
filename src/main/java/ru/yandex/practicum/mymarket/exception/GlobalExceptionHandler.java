package ru.yandex.practicum.mymarket.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
@Controller
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MarketException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Rendering> handleMarketException(MarketException ex, Locale locale) {

        log.warn("Business exception occurred. errorCode={}", ex.getErrorCode());

        String message = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, locale);

        return Mono.just(Rendering.view("error").modelAttribute("message", message).build());
    }

    @ExceptionHandler({ItemNotFoundException.class, OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Rendering> handleNotFoundException(MarketException ex, Locale locale) {

        log.warn("Not found exception occurred. errorCode={}", ex.getErrorCode());

        String message = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, locale);

        return Mono.just(Rendering.view("error").modelAttribute("message", message).build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Rendering> handleConstraintViolationException(
            ConstraintViolationException ex, Locale locale) {

        log.warn("Validation exception occurred. message={}", ex.getMessage());

        String message =
                messageSource.getMessage(ErrorCode.INVALID_PAGE_SIZE.getMessageKey(), null, locale);

        return Mono.just(Rendering.view("error").modelAttribute("message", message).build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Rendering> handleException(Exception ex, Locale locale) {

        log.error("Unexpected exception occurred", ex);

        String message =
                messageSource.getMessage(ErrorCode.INTERNAL_ERROR.getMessageKey(), null, locale);

        return Mono.just(Rendering.view("error").modelAttribute("message", message).build());
    }
}
