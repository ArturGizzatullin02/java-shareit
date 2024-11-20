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
        log.info("CreateBooking: {} started", bookingDto);
        BookingFullDto result = bookingService.create(userId, bookingDto);
        log.info("CreateBooking with id {} for user with id {} and item with id {} finished",
                result.getId(), result.getBooker().getId(), result.getItem().getId());
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approve(@RequestHeader(USER_ID_HEADER) long userId,
                                  @PathVariable long bookingId,
                                  @RequestParam boolean approved) {
        log.info("ApproveBooking: {} for userId {} started", bookingId, userId);
        BookingFullDto result = bookingService.approve(userId, bookingId, approved);
        log.info("ApproveBooking with id {} for user with id {} and item with id {} finished",
                result.getId(), result.getBooker().getId(), result.getItem().getId());
        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto get(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long bookingId) {
        log.info("GetBooking: {} started", bookingId);
        BookingFullDto result = bookingService.get(userId, bookingId);
        log.info("GetBooking with id {} for user with id {} and item with id {} finished",
                result.getId(), result.getBooker().getId(), result.getItem().getId());
        return result;
    }

    @GetMapping
    public Collection<BookingFullDto> getAllByUserIdWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                              @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GetAllBookings started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        Collection<BookingFullDto> result = bookingService.getAllByUserIdWithState(userId, stateParameter);
        log.info("GetAllBookings for user with id {} finished", userId);
        return result;
    }

    @GetMapping("/owner")
    public Collection<BookingFullDto> getAllForUserItemsWithState(@RequestHeader(USER_ID_HEADER) long userId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GetAllForUserItemsWithState started");
        BookingStateParameter stateParameter = BookingStateParameter.from(state);
        Collection<BookingFullDto> result = bookingService.getAllForUserItemsWithState(userId, stateParameter);
        log.info("GetAllForUserItemsWithState with userId {} finished", userId);
        return result;
    }
}
