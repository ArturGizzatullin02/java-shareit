package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEndConstraint, BookingCreateDto> {

    @Override
    public boolean isValid(BookingCreateDto booking, ConstraintValidatorContext constraintValidatorContext) {
        if (booking == null) {
            return true;
        }

        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (start != null && end != null) {
            if (start.isEqual(end)) {
                return false;
            }
            return start.isBefore(end);
        }

        return false;
    }
}
