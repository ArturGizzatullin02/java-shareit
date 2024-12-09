package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemServiceImplSpringBootTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void getItemsOfOwner() {

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

        ItemFullDto itemFullDto1 = itemService.create(userFullDto1.getId(), itemCreateDto1);

        ItemFullDto itemFullDto2 = itemService.create(userFullDto2.getId(), itemCreateDto2);
        ItemFullDto itemFullDto3 = itemService.create(userFullDto2.getId(), itemCreateDto3);

        ItemFullDto itemFullDto4 = itemService.create(userFullDto1.getId(), itemCreateDto4);
        ItemFullDto itemFullDto5 = itemService.create(userFullDto1.getId(), itemCreateDto5);
        ItemFullDto itemFullDto6 = itemService.create(userFullDto1.getId(), itemCreateDto6);

        Collection<ItemFullDto> itemsOfOwner1 = itemService.getItemsOfOwner(userFullDto1.getId());

        assertEquals(4, itemsOfOwner1.size());
        assertFalse(itemsOfOwner1.contains(itemFullDto2));
        assertFalse(itemsOfOwner1.contains(itemFullDto3));

        assertThrows(UserNotFoundException.class, () -> itemService.getItemsOfOwner(-1));
    }
}