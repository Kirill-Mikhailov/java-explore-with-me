package ru.practicum.ewm.exception;

public class IncorrectlyMadeRequest extends RuntimeException {
    public IncorrectlyMadeRequest(String message) {
        super(message);
    }
}
