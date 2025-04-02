package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.Collection;

public interface BookingService {

    BookingFullDto create(long userId, BookingCreateDto bookingDto);

    BookingFullDto approve(long userId, long id, boolean approved);

    BookingFullDto get(long userId, long id);

    Collection<BookingFullDto> getAllByUserIdWithState(long userId, BookingStateParameter state);

    Collection<BookingFullDto> getAllForUserItemsWithState(long userId, BookingStateParameter state);
}
