package ru.practicum.ewm.exception;

public class CategoryIsNotEmptyException extends ConditionsAreNotMetException {
    public CategoryIsNotEmptyException(String message) {
        super(message);
    }
}
