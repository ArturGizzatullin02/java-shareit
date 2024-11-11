package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public BookingFullDto create(long userId, BookingCreateDto bookingDto) {
        log.info("[BOOKING SERVICE] Booking create starting: {} for user {}", bookingDto, userId);
        Long itemId = bookingDto.getItemId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Item with id " + itemId + " is not available");
        }
        Booking booking = mapper.map(bookingDto, Booking.class);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("[BOOKING SERVICE] Booking created: {}", savedBooking);
        return mapper.map(savedBooking, BookingFullDto.class);
    }

    @Override
    @Transactional
    public BookingFullDto approve(long userId, long id, boolean approved) {
        log.info("[BOOKING SERVICE] Booking approve starting for booking: {} and user {}", id, userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found"));
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new PermissionDeniedException("User with id "
                    + userId + " is not allowed to approve booking with id " + id);
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.info("[BOOKING SERVICE] Booking approved: {}", savedBooking);
        return mapper.map(savedBooking, BookingFullDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingFullDto get(long userId, long id) {
        log.info("[BOOKING SERVICE] Booking get starting for booking: {} and user {}", id, userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found"));
        if (!booking.getItem().getUser().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new PermissionDeniedException("User with id "
                    + userId + " is not allowed to get booking with id " + id);
        }
        log.info("[BOOKING SERVICE] Booking get finished for: {}", booking);
        return mapper.map(booking, BookingFullDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingFullDto> getAllByUserIdWithState(long userId, BookingStateParameter state) {
        log.info("[BOOKING SERVICE] Booking getAllByUserIdWithState starting for user: {} and state: {}", userId, state);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result = Collections.emptyList();

        if (state.name().equals("ALL")) {
            result = bookingRepository.findAllByBookerId(userId);
        } else if (state.name().equals("CURRENT")) {
            result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    now, now);
        } else if (state.name().equals("PAST")) {
            result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
        } else if (state.name().equals("FUTURE")) {
            result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
        } else if (state.name().equals("WAITING")) {
            result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if (state.name().equals("REJECTED")) {
            result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }

        log.info("[BOOKING SERVICE] Booking getAllByUserIdWithState finished for user: {} and state: {}", userId, state);
        return mapper.map(result, new TypeToken<Collection<BookingFullDto>>() {
        }.getType());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingFullDto> getAllForUserItemsWithState(long userId, BookingStateParameter state) {
        log.info("[BOOKING SERVICE] Booking getAllForUserItemsWithState starting for user: {} and state: {}", userId, state);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        Collection<Item> userItems = itemRepository.findAllByUserId(userId);

        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result = Collections.emptyList();

        if (state.name().equals("ALL")) {
            result = bookingRepository.findAllByBookerIdAndItemIn(userId, userItems);
        } else if (state.name().equals("CURRENT")) {
            result = bookingRepository
                    .findAllByBookerIdAndItemInAndStartBeforeAndEndAfterOrderByStartDesc(userId, userItems, now, now);
        } else if (state.name().equals("PAST")) {
            result = bookingRepository
                    .findAllByBookerIdAndItemInAndEndBeforeOrderByStartDesc(userId, userItems, now);
        } else if (state.name().equals("FUTURE")) {
            result = bookingRepository
                    .findAllByBookerIdAndItemInAndStartAfterOrderByStartDesc(userId, userItems, now);
        } else if (state.name().equals("WAITING")) {
            result = bookingRepository
                    .findAllByBookerIdAndItemInAndStatusOrderByStartDesc(userId, userItems, BookingStatus.WAITING);
        } else if (state.name().equals("REJECTED")) {
            result = bookingRepository
                    .findAllByBookerIdAndItemInAndStatusOrderByStartDesc(userId, userItems, BookingStatus.REJECTED);
        }
        log.info("[BOOKING SERVICE] Booking getAllForUserItemsWithState finished for user: {} and state: {}", userId, state);
        return mapper.map(result, new TypeToken<Collection<BookingFullDto>>() {
        }.getType());
    }
}
