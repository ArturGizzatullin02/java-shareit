package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long bookingId = 1L;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

    private User user;
    private Item item;
    private BookingCreateDto bookingCreateDto;
    private Booking booking;

    private ItemForRequestDto itemForRequestDto;
    private UserFullDto userFullDto;
    private BookingFullDto bookingFullDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        item = Item.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .user(user)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(startDate)
                .end(endDate)
                .build();


        booking = Booking.builder()
                .id(bookingId)
                .start(startDate)
                .end(endDate)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        itemForRequestDto = ItemForRequestDto.builder()
                .id(itemId)
                .name("Laptop")
                .userId(userId)
                .build();

        userFullDto = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();


        bookingFullDto = BookingFullDto.builder()
                .id(bookingId)
                .start(startDate)
                .end(endDate)
                .item(itemForRequestDto)
                .booker(userFullDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    @DisplayName("Метод создания брони должен возвращать бронь")
    void createWhenSuccess() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(mapper.map(bookingCreateDto, Booking.class)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(mapper.map(booking, BookingFullDto.class)).thenReturn(bookingFullDto);

        BookingFullDto result = bookingService.create(userId, bookingCreateDto);

        assertNotNull(result);
        assertEquals(bookingFullDto, result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Бронирование вещи, которая недоступна для аренды, должно выбросить исключение ItemNotAvailableException")
    void createWhenItemUnavailable() {
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, () -> bookingService.create(userId, bookingCreateDto));
        assertEquals("Item with id " + itemId + " is not available", exception.getMessage());
    }

    @Test
    @DisplayName("Метод подтверждения брони должен возвращать бронь со статусом APPROVED")
    void approveWhenSuccess() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(mapper.map(booking, BookingFullDto.class)).thenReturn(bookingFullDto);

        BookingFullDto result = bookingService.approve(userId, bookingId, true);
        assertNotNull(result);
        assertEquals(bookingFullDto, result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Метод подтверждения брони должен возвращать исключение PermissionDeniedException при попытке подтвердить бронь для чужой вещи")
    void approveWhenUserIsNotOwner() {
        long notOwnerId = 999L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> bookingService.approve(notOwnerId, bookingId, true));
        assertEquals("User with id " + notOwnerId + " is not allowed to approve booking with id " + bookingId, exception.getMessage());
    }

    @Test
    @DisplayName("Метод для подтверждения брони должен возвращать BookingNotFoundException, если бронирования не существует")
    void approveWhenBookingNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> bookingService.approve(userId, bookingId, true));
        assertEquals("Booking with id " + bookingId + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Метод получения брони должен возвращать бронь")
    void getWhenSuccess() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(mapper.map(booking, BookingFullDto.class)).thenReturn(bookingFullDto);

        BookingFullDto result = bookingService.get(userId, bookingId);
        assertNotNull(result);
        assertEquals(bookingFullDto, result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Метод получения брони должен возвращать исключение PermissionDeniedException при попытке получить чужую бронь или бронь для чужой вещи")
    void getWhenUserIsNotOwner() {
        long notOwnerId = 999L;
        when(userRepository.existsById(notOwnerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> bookingService.get(notOwnerId, bookingId));
        assertEquals("User with id " + notOwnerId + " is not allowed to get booking with id " + bookingId, exception.getMessage());
    }

    @Test
    @DisplayName("Метод получения броней пользователя с состоянием ALL должен возвращать список всех броней")
    void getAllByUserIdWithStateAll_WhenSuccess() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerId(userId)).thenReturn(Collections.singletonList(booking));

        doReturn(Collections.singletonList(bookingFullDto))
                .when(mapper).map(anyCollection(), eq(new TypeToken<Collection<BookingFullDto>>() {
                }.getType()));

        when(bookingService.getAllByUserIdWithState(userId, BookingStateParameter.ALL)).thenReturn(Collections.singletonList(bookingFullDto));

        Collection<BookingFullDto> result = bookingService.getAllByUserIdWithState(userId, BookingStateParameter.ALL);

        assertNotNull(result);
        assertEquals(Collections.singletonList(bookingFullDto), result);
    }

    @Test
    @DisplayName("Метод получения броней пользователя с состоянием ALL должен выбрасывать исключение UserNotFoundException при попытке получить брони для несуществующего пользователя")
    void getAllByUserIdWithStateAll_WhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllByUserIdWithState(userId, BookingStateParameter.ALL));
    }

    @Test
    @DisplayName("Метод получения броней для вещей пользователя с состоянием ALL должен возвращать список броней для всех вещей")
    void getAllForUserItemsWithStateAll_WhenSuccess() {
        when(userRepository.existsById(userId)).thenReturn(true);
        List<Item> userItems = Collections.singletonList(item);

        when(itemRepository.findAllByUserId(userId)).thenReturn(userItems);

        doReturn(Collections.singletonList(bookingFullDto))
                .when(mapper).map(anyCollection(), eq(new TypeToken<Collection<BookingFullDto>>() {
                }.getType()));

        when(bookingService.getAllForUserItemsWithState(userId, BookingStateParameter.ALL)).thenReturn(Collections.singletonList(bookingFullDto));

        Collection<BookingFullDto> result = bookingService.getAllForUserItemsWithState(userId, BookingStateParameter.ALL);

        assertNotNull(result);
        assertEquals(Collections.singletonList(bookingFullDto), result);
    }

    @Test
    @DisplayName("Метод получения броней для вещей пользователя с состоянием ALL должен выбрасывать исключение UserNotFoundException при попытке получить брони для несуществующего пользователя")
    void getAllForUserItemsWithStateAll_WhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllForUserItemsWithState(userId, BookingStateParameter.ALL));
    }
}