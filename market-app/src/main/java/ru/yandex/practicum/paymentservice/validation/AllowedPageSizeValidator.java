package ru.yandex.practicum.paymentservice.validation;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedPageSizeValidator implements ConstraintValidator<AllowedPageSize, Integer> {

    private static final Set<Integer> ALLOWED_PAGE_SIZES = Set.of(2, 5, 10, 20, 50, 100);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return ALLOWED_PAGE_SIZES.contains(value);
    }
}
