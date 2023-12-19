package ru.practicum.ewm.exception;

public class ConditionsAreNotMetException extends RuntimeException {
    public ConditionsAreNotMetException(String message) {
        super(message);
    }
}
