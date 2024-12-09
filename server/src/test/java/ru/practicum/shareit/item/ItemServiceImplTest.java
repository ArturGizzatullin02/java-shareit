package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.CommentForNotStartedBookingException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModelMapper mapper;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long commentId = 1L;
    private Long lastBookingId = 1L;
    private Long nextBookingId = 2L;
    private LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    private LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

    private User user;
    private Item item;
    private Comment comment;

    private ItemCreateDto itemCreateDto;
    private CommentCreateDto commentCreateDto;
    private CommentFullDto commentFullDto;
    private ItemFullDto itemFullDto;
    private UserFullDto userFullDto;
    private BookingShortDto lastBookingShortDto;
    private BookingShortDto nextBookingShortDto;
    private CommentShortDto commentShortDto;

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

        comment = Comment.builder()
                .id(commentId)
                .text("very important comment")
                .item(item)
                .author(user)
                .build();

        itemCreateDto = ItemCreateDto.builder()
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .build();

        commentCreateDto = CommentCreateDto.builder()
                .text("very important comment")
                .build();

        userFullDto = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        commentFullDto = CommentFullDto.builder()
                .id(commentId)
                .text("very important comment")
                .created(LocalDateTime.now())
                .authorName(userFullDto.getName())
                .build();

        lastBookingShortDto = BookingShortDto.builder()
                .id(lastBookingId)
                .start(startDate)
                .end(endDate)
                .build();

        nextBookingShortDto = BookingShortDto.builder()
                .id(nextBookingId)
                .start(startDate.plusMonths(1))
                .end(endDate.plusMonths(2))
                .build();

        commentShortDto = CommentShortDto.builder()
                .id(commentId)
                .text("very important comment")
                .build();

        itemFullDto = ItemFullDto.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .owner(userFullDto)
                .build();
    }

    @Test
    @DisplayName("Создание вещи должно возвращать ItemFullDto")
    void createWhenSuccess() {
        when(mapper.map(itemCreateDto, Item.class)).thenReturn(item);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(mapper.map(item, ItemFullDto.class)).thenReturn(itemFullDto);

        ItemFullDto result = itemService.create(userId, itemCreateDto);

        assertNotNull(result);
        assertEquals(itemFullDto, result);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Создание вещи должно выбрасывать UserNotFoundException при попытке создания вещи для несуществующего пользователя")
    void createWhenUserNotFound() {
        long notFoundUserId = 999L;

        assertThrows(UserNotFoundException.class, () -> itemService.create(notFoundUserId, itemCreateDto));
    }

    @Test
    @DisplayName("Обновление описания вещи должно вернуть ItemFullDto с обновленным описанием")
    void update() {

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Laptop")
                .description("Macbook Pro M4")
                .available(true)
                .build();

        Item itemUpdated = Item.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Pro M4")
                .available(true)
                .user(user)
                .build();

        ItemFullDto itemFullDtoUpdated = ItemFullDto.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Pro M4")
                .available(true)
                .owner(userFullDto)
                .lastBooking(lastBookingShortDto)
                .nextBooking(nextBookingShortDto)
                .build();


        when(mapper.map(itemUpdateDto, Item.class)).thenReturn(itemUpdated);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdated);
        when(mapper.map(itemUpdated, ItemFullDto.class)).thenReturn(itemFullDtoUpdated);

        ItemFullDto result = itemService.update(userId, itemUpdateDto, itemId);
        assertNotNull(result);
        assertEquals(itemFullDtoUpdated, result);
    }

    @Test
    @DisplayName("Обновление вещи должно выбрасывать UserNotFoundException при попытке обновления вещи для несуществующего пользователя")
    void updateWhenUserNotFound() {
        long notFoundUserId = 999L;
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Laptop")
                .description("Macbook Pro M4")
                .available(true)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemService.update(notFoundUserId, itemUpdateDto, itemId));
        assertEquals("User with id " + notFoundUserId + " not found", exception.getMessage());
    }

    @Test
    void getWhenSuccess() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.singletonList(comment));
        when(mapper.map(anyCollection(), eq(new TypeToken<Collection<CommentShortDto>>() {
        }.getType()))).thenReturn(Collections.singletonList(commentShortDto));
        when(mapper.map(item, ItemFullDto.class)).thenReturn(itemFullDto);

        long someBookingId = 3L;

        Booking someBooking = Booking.builder()
                .id(someBookingId)
                .start(startDate.minusMonths(1))
                .end(endDate.minusMonths(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        Booking lastBooking = Booking.builder()
                .id(lastBookingId)
                .start(startDate)
                .end(endDate)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking nextBooking = Booking.builder()
                .id(nextBookingId)
                .start(startDate.plusMonths(1))
                .end(endDate.plusMonths(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        List<Booking> bookings = List.of(someBooking, lastBooking, nextBooking);

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);

        when(mapper.map(Optional.of(lastBooking), BookingShortDto.class)).thenReturn(lastBookingShortDto);
        when(mapper.map(Optional.of(nextBooking), BookingShortDto.class)).thenReturn(nextBookingShortDto);

        ItemFullDto result = itemService.get(itemId);
        assertNotNull(result);
        assertEquals(itemFullDto, result);
    }

    @Test
    void getWhenItemNotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> itemService.get(itemId));
        assertEquals("Item with id " + itemId + " not found", exception.getMessage());
    }

    @Test
    void getItemsOfOwner() {
        when(userRepository.existsById(userId)).thenReturn(true);

        List<Item> itemsOfOwner = List.of(item);
        List<ItemFullDto> itemsOfOwnerFullDto = List.of(itemFullDto);

        when(itemRepository.findAllByUserId(userId)).thenReturn(itemsOfOwner);

        when(mapper.map(anyCollection(), eq(new TypeToken<Collection<ItemFullDto>>() {
        }.getType()))).thenReturn(itemsOfOwnerFullDto);


        List<Comment> comments = List.of(comment);
        List<CommentShortDto> commentsShortDtos = List.of(commentShortDto);

        when(commentRepository.findAllByItemIn(itemsOfOwner)).thenReturn(comments);

        when(mapper.map(anyCollection(), eq(new TypeToken<Collection<CommentShortDto>>() {
        }.getType()))).thenReturn(commentsShortDtos);

        long someBookingId = 3L;

        Booking someBooking = Booking.builder()
                .id(someBookingId)
                .start(startDate.minusMonths(1))
                .end(endDate.minusMonths(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        Booking lastBooking = Booking.builder()
                .id(lastBookingId)
                .start(startDate)
                .end(endDate)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking nextBooking = Booking.builder()
                .id(nextBookingId)
                .start(startDate.plusMonths(1))
                .end(endDate.plusMonths(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        List<Booking> bookings = List.of(someBooking, lastBooking, nextBooking);

        when(bookingRepository.findAllByItemIdIn(anyList())).thenReturn(bookings);

        mapper.map(lastBooking, BookingShortDto.class);
        mapper.map(nextBooking, BookingShortDto.class);

        Collection<ItemFullDto> result = itemService.getItemsOfOwner(userId);
        assertNotNull(result);
        assertEquals(itemsOfOwnerFullDto, result);
    }

    @Test
    void search() {
        Collection<Item> items = Collections.singletonList(item);
        List<ItemFullDto> itemFullDtos = Collections.singletonList(itemFullDto);
        when(itemRepository.searchByDescriptionOrName(anyString())).thenReturn(items);
        when(mapper.map(items, new TypeToken<Collection<ItemFullDto>>() {
        }.getType())).thenReturn(Collections.singletonList(itemFullDto));

        Collection<ItemFullDto> result = itemService.search("Laptop");

        assertNotNull(result);
        assertEquals(itemFullDtos, result);
    }

    @Test
    void createComment() {
        when(mapper.map(commentCreateDto, Comment.class)).thenReturn(comment);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        long someBookingId = 3L;

        Booking someBooking = Booking.builder()
                .id(someBookingId)
                .start(startDate.minusMonths(1))
                .end(endDate.minusMonths(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        Booking lastBooking = Booking.builder()
                .id(lastBookingId)
                .start(startDate)
                .end(endDate)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking nextBooking = Booking.builder()
                .id(nextBookingId)
                .start(startDate.plusMonths(1))
                .end(endDate.plusMonths(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        List<Booking> bookings = List.of(someBooking, lastBooking, nextBooking);

        when(bookingRepository.findAllByBookerId(userId)).thenReturn(bookings);
        when(mapper.map(any(Comment.class), eq(CommentFullDto.class))).thenReturn(commentFullDto);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentFullDto result = itemService.createComment(itemId, userId, commentCreateDto);
        assertNotNull(result);
        assertEquals(commentFullDto, result);
    }

    @Test
    void createCommentForNotStartedBooking() {
        when(mapper.map(commentCreateDto, Comment.class)).thenReturn(comment);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        long someBookingId = 3L;

        Booking someBooking = Booking.builder()
                .id(someBookingId)
                .start(startDate.plusMonths(1))
                .end(endDate.plusMonths(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> bookings = List.of(someBooking);

        when(bookingRepository.findAllByBookerId(userId)).thenReturn(bookings);

        assertThrows(CommentForNotStartedBookingException.class, () -> itemService.createComment(itemId, userId, commentCreateDto));
    }
}