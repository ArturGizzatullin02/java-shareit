package ru.practicum.shareit.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEndConstraint {

    String message() default "Стартовое время должно быть раньше конечного, при этом они оба не null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
