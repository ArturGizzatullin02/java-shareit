package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.UnknownBookingStateException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto create(@RequestHeader(USER_ID_HEADER) long userId,
                                 @RequestBody @Valid BookingCreateDto bookingDto) {
        log.info("[BOOKING CONTROLLER] createBooking: {} started", bookingDto);
        BookingFullDto result = bookingService.create(userId, bookingDto);
        log.info("[BOOKING CONTROLLER] createBooking: {} finished", result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approve(@RequestHeader(USER_ID_HEADER) long userId,
                                  @PathVariable long bookingId,
                                  @RequestParam boolean approved) {
        log.info("[BOOKING CONTROLLER] approveBooking: {} for userId {} started", bookingId, userId);
        BookingFullDto result = bookingService.approve(userId, bookingId, approved);
        log.info("[BOOKING CONTROLLER] approveBooking: {} finished", result);
        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto get(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long bookingId) {
        log.info("[BOOKING CONTROLLER] getBooking: {} started", bookingId);
        BookingFullDto result = bookingService.get(userId, bookingId);
        log.info("[BOOKING CONTROLLER] getBooking: {} finished", result);
        return result;
    }

    @GetMapping
    public Collection<BookingFullDto> getAllByUserIdWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                              @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("[BOOKING CONTROLLER] getAllBookings started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        if (stateParameter == null) {
            throw new UnknownBookingStateException("Unknown state parameter");
        }
        Collection<BookingFullDto> result = bookingService.getAllByUserIdWithState(userId, stateParameter);
        log.info("[BOOKING CONTROLLER] getAllBookings: {} finished", result);
        return result;
    }

    @GetMapping("/owner")
    public Collection<BookingFullDto> getAllForUserItemsWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("[BOOKING CONTROLLER] getAllForUserItemsWithState started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        if (stateParameter == null) {
            throw new UnknownBookingStateException("Unknown state parameter");
        }
        Collection<BookingFullDto> result = bookingService.getAllForUserItemsWithState(userId, stateParameter);
        log.info("[BOOKING CONTROLLER] getAllForUserItemsWithState with result: {} finished", result);
        return result;
    }
}
