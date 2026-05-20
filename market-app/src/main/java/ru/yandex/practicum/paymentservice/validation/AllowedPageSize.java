package ru.yandex.practicum.paymentservice.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedPageSizeValidator.class)
public @interface AllowedPageSize {

    String message() default "Invalid page size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
