package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class BookingServiceImplSpringBootTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("Получение бронирований с соответствующими параметрами. Должно вернуть только подходящие бронирования," +
            " которые были совершены именно пользователем, который делает запрос. А при попытке получить бронирования," +
            " для несуществующего пользователя, должно быть выброшено исключение UserNotFoundException")
    void getAllForUserItemsWithState() {
        UserCreateDto userCreateDto1 = UserCreateDto.builder()
                .name("John")
                .email("john@gmail.com")
                .build();

        UserCreateDto userCreateDto2 = UserCreateDto.builder()
                .name("Nicolas")
                .email("nicolas@gmail.com")
                .build();

        UserFullDto userFullDto1 = userService.create(userCreateDto1);
        UserFullDto userFullDto2 = userService.create(userCreateDto2);

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name("Laptop")
                .description("Macbook")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name("Book")
                .description("Harry Potter")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name("Phone")
                .description("Google Phone")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto4 = ItemCreateDto.builder()
                .name("Hoodie")
                .description("Stussy")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto5 = ItemCreateDto.builder()
                .name("T-Shirt")
                .description("Tommy Hilfiger")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto6 = ItemCreateDto.builder()
                .name("Bag")
                .description("Nike")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto7 = ItemCreateDto.builder()
                .name("Watch")
                .description("Rolex")
                .available(true)
                .build();

        ItemFullDto itemFullDto1 = itemService.create(userFullDto1.getId(), itemCreateDto1);
        ItemFullDto itemFullDto2 = itemService.create(userFullDto1.getId(), itemCreateDto2);

        ItemFullDto itemFullDto3 = itemService.create(userFullDto2.getId(), itemCreateDto3);

        ItemFullDto itemFullDto4 = itemService.create(userFullDto1.getId(), itemCreateDto4);
        ItemFullDto itemFullDto5 = itemService.create(userFullDto1.getId(), itemCreateDto5);
        ItemFullDto itemFullDto6 = itemService.create(userFullDto1.getId(), itemCreateDto6);
        ItemFullDto itemFullDto7 = itemService.create(userFullDto1.getId(), itemCreateDto7);

        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(itemFullDto1.getId())
                .start(LocalDateTime.of(2021, 1, 1, 0, 0))
                .end(LocalDateTime.of(2021, 1, 2, 0, 0))
                .build();

        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(itemFullDto2.getId())
                .start(LocalDateTime.of(2022, 1, 1, 0, 0))
                .end(LocalDateTime.of(2022, 1, 2, 0, 0))
                .build();

        BookingCreateDto bookingCreateDto3 = BookingCreateDto.builder()
                .itemId(itemFullDto3.getId())
                .start(LocalDateTime.of(2021, 1, 1, 0, 0))
                .end(LocalDateTime.of(2021, 1, 2, 0, 0))
                .build();

        BookingCreateDto bookingCreateDto4 = BookingCreateDto.builder()
                .itemId(itemFullDto4.getId())
                .start(LocalDateTime.of(2099, 1, 1, 0, 0))
                .end(LocalDateTime.of(2099, 1, 2, 0, 0))
                .build();

        BookingCreateDto bookingCreateDto5 = BookingCreateDto.builder()
                .itemId(itemFullDto5.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMonths(1))
                .build();

        BookingCreateDto bookingCreateDto6 = BookingCreateDto.builder()
                .itemId(itemFullDto6.getId())
                .start(LocalDateTime.of(2090, 1, 1, 0, 0))
                .end(LocalDateTime.of(2090, 1, 2, 0, 0))
                .build();

        BookingCreateDto bookingCreateDto7 = BookingCreateDto.builder()
                .itemId(itemFullDto7.getId())
                .start(LocalDateTime.of(2090, 1, 1, 0, 0))
                .end(LocalDateTime.of(2090, 1, 2, 0, 0))
                .build();

        BookingFullDto bookingFullDtoCreated1 = bookingService.create(userFullDto1.getId(), bookingCreateDto1);
        BookingFullDto bookingFullDtoCreated2 = bookingService.create(userFullDto1.getId(), bookingCreateDto2);
        BookingFullDto bookingFullDtoCreated3 = bookingService.create(userFullDto2.getId(), bookingCreateDto3);
        BookingFullDto bookingFullDtoCreated4 = bookingService.create(userFullDto1.getId(), bookingCreateDto4);
        BookingFullDto bookingFullDtoCreated5 = bookingService.create(userFullDto1.getId(), bookingCreateDto5);

        BookingFullDto bookingFullDtoCreated6 = bookingService.create(userFullDto1.getId(), bookingCreateDto6);

        BookingFullDto bookingFullDtoCreated7 = bookingService.create(userFullDto1.getId(), bookingCreateDto7);

        BookingFullDto bookingFullDto1 = bookingService.approve(userFullDto1.getId(), bookingFullDtoCreated1.getId(), true);
        BookingFullDto bookingFullDto2 = bookingService.approve(userFullDto1.getId(), bookingFullDtoCreated2.getId(), true);

        BookingFullDto bookingFullDto3 = bookingService.approve(userFullDto2.getId(), bookingFullDtoCreated3.getId(), true);

        BookingFullDto bookingFullDto4 = bookingService.approve(userFullDto1.getId(), bookingFullDtoCreated4.getId(), true);
        BookingFullDto bookingFullDto5 = bookingService.approve(userFullDto1.getId(), bookingFullDtoCreated5.getId(), true);

        BookingFullDto bookingFullDto7 = bookingService.approve(userFullDto1.getId(), bookingFullDtoCreated7.getId(), false);

        Collection<BookingFullDto> allForUserItemsWithStatePast = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.PAST);
        assertEquals(2, allForUserItemsWithStatePast.size());
        assertTrue(allForUserItemsWithStatePast.containsAll(List.of(bookingFullDto1, bookingFullDto2)));

        Collection<BookingFullDto> allForUserItemsWithStateFuture = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.FUTURE);
        assertEquals(3, allForUserItemsWithStateFuture.size());
        assertTrue(allForUserItemsWithStateFuture.containsAll(List.of(bookingFullDto4, bookingFullDtoCreated6, bookingFullDto7)));

        Collection<BookingFullDto> allForUserItemsWithStateCurrent = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.CURRENT);
        assertEquals(1, allForUserItemsWithStateCurrent.size());
        assertTrue(allForUserItemsWithStateCurrent.contains(bookingFullDto5));

        Collection<BookingFullDto> allForUserItemsWithStateWaiting = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.WAITING);
        assertEquals(1, allForUserItemsWithStateWaiting.size());
        assertTrue(allForUserItemsWithStateWaiting.contains(bookingFullDtoCreated6));

        Collection<BookingFullDto> allForUserItemsWithStateRejected = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.REJECTED);
        assertEquals(1, allForUserItemsWithStateRejected.size());
        assertTrue(allForUserItemsWithStateRejected.contains(bookingFullDto7));

        Collection<BookingFullDto> allForUserItemsWithStateAll = bookingService.getAllForUserItemsWithState(userFullDto1.getId(), BookingStateParameter.ALL);
        assertEquals(6, allForUserItemsWithStateAll.size());
        assertTrue(allForUserItemsWithStateAll.containsAll(List
                .of(bookingFullDto1, bookingFullDto2, bookingFullDto4, bookingFullDto5, bookingFullDtoCreated6, bookingFullDto7)));

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllForUserItemsWithState(12345678910L, BookingStateParameter.WAITING));
    }
}