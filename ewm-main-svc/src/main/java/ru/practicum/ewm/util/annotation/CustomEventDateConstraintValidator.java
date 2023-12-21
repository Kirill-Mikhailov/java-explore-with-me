package ru.practicum.ewm.util.annotation;

import ru.practicum.ewm.exception.EventDateNotValidException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Objects;

public class CustomEventDateConstraintValidator implements ConstraintValidator<CustomEventDate, LocalDateTime> {

    @Override
    public void initialize(CustomEventDate eventDate) {
    }

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime min = LocalDateTime.now().plusHours(2);
        if (!Objects.isNull(date) && date.isBefore(min)) {
            throw new EventDateNotValidException("Field: eventDate. Error: " +
                    "Событие не может быть раньше, чем через два часа от текущего момента. Value: " + date);
        }
        return true;
    }
}
