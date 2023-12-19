package ru.practicum.ewm.exception;

public class EventDateNotValidException extends IncorrectlyMadeRequest {
    public EventDateNotValidException(String message) {
        super(message);
    }
}
