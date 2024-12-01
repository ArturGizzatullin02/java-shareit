package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice()
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.warn("ERROR  ", e);
        return new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace()));
    }

    @ExceptionHandler(value = {UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final Exception e) {
        log.warn("ERROR  ", e);
        return new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(final UserAlreadyExistsException e) {
        log.warn("ERROR  ", e);
        return new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlePermissionDeniedException(final PermissionDeniedException e) {
        log.warn("ERROR  ", e);
        return new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace()));
    }

    @ExceptionHandler(value = {ItemNotAvailableException.class, MethodArgumentNotValidException.class, CommentForNotStartedBookingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        log.warn("ERROR  ", e);
        return new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace()));
    }
}
