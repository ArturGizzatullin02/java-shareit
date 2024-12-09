package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemRequestServiceImplSpringBootTest {

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Test
    void getAllItemRequests() {
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

        LocalDateTime now = LocalDateTime.now();

        ItemRequestCreateDto itemRequestCreateDto1 = ItemRequestCreateDto.builder()
                .description("Macbook")
                .created(now)
                .build();

        ItemRequestCreateDto itemRequestCreateDto2 = ItemRequestCreateDto.builder()
                .description("Harry Potter book")
                .created(now)
                .build();

        ItemRequestCreateResponseDto itemRequest1 = itemRequestService.createItemRequest(itemRequestCreateDto1, userFullDto1.getId());
        ItemRequestCreateResponseDto itemRequest2 = itemRequestService.createItemRequest(itemRequestCreateDto2, userFullDto1.getId());

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name("Laptop")
                .description("Macbook")
                .available(true)
                .requestId(itemRequest1.getId())
                .build();

        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name("Book")
                .description("Harry Potter")
                .available(true)
                .requestId(itemRequest2.getId())
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

        ItemFullDto itemFullDto1 = itemService.create(userFullDto1.getId(), itemCreateDto1);
        ItemFullDto itemFullDto2 = itemService.create(userFullDto1.getId(), itemCreateDto2);

        ItemFullDto itemFullDto3 = itemService.create(userFullDto2.getId(), itemCreateDto3);
        ItemFullDto itemFullDto4 = itemService.create(userFullDto2.getId(), itemCreateDto4);

        ItemFullDto itemFullDto5 = itemService.create(userFullDto1.getId(), itemCreateDto5);
        ItemFullDto itemFullDto6 = itemService.create(userFullDto1.getId(), itemCreateDto6);

        ItemForRequestDto expectedItemForRequestDto1 = ItemForRequestDto.builder()
                .id(1L)
                .name("Laptop")
                .userId(userFullDto1.getId())
                .build();

        ItemForRequestDto expectedItemForRequestDto2 = ItemForRequestDto.builder()
                .id(2L)
                .name("Book")
                .userId(userFullDto1.getId())
                .build();

        Collection<ItemRequestGetDto> allItemRequests = itemRequestService.getAllItemRequests();

        assertThat(allItemRequests)
                .flatExtracting(ItemRequestGetDto::getItems)
                .extracting("name", "userId")
                .contains(
                        tuple(expectedItemForRequestDto1.getName(), expectedItemForRequestDto1.getUserId()),
                        tuple(expectedItemForRequestDto2.getName(), expectedItemForRequestDto2.getUserId())
                );

        assertEquals(2, allItemRequests.size());
    }
}