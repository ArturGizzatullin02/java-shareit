package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody @Valid BookingCreateDto bookingDto) {
        log.info("CreateBooking: {} started", bookingDto);
        ResponseEntity<Object> result = bookingClient.create(userId, bookingDto);
        log.info("CreateBooking for user with id {} finished",
                userId);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        log.info("ApproveBooking: {} for userId {} started", bookingId, userId);
        ResponseEntity<Object> result = bookingClient.approve(userId, bookingId, approved);
        log.info("ApproveBooking with id {} for user with id {} finished",
                bookingId, userId);
        return result;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID_HEADER) long userId,
                                      @PathVariable long bookingId) {
        log.info("GetBooking: {} started", bookingId);
        ResponseEntity<Object> result = bookingClient.get(userId, bookingId);
        log.info("GetBooking with id {} for user with id {} finished",
                bookingId, userId);
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserIdWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                          @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GetAllBookings started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        ResponseEntity<Object> result = bookingClient.getAllByUserIdWithState(userId, stateParameter);
        log.info("GetAllBookings for user with id {} finished", userId);
        return result;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForUserItemsWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                              @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GetAllForUserItemsWithState started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        ResponseEntity<Object> result = bookingClient.getAllForUserItemsWithState(userId, stateParameter);
        log.info("GetAllForUserItemsWithState with userId {} finished", userId);
        return result;
    }
}
