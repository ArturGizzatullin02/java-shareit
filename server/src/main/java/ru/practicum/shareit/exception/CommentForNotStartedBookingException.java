package ru.practicum.shareit.exception;

public class CommentForNotStartedBookingException extends RuntimeException {
    public CommentForNotStartedBookingException(String message) {
        super(message);
    }
}
