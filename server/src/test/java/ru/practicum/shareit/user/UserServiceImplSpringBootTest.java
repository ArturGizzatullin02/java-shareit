package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceImplSpringBootTest {

    @Autowired
    UserService userService;

    @Test
    void update() {
        UserCreateDto userCreateDto1 = UserCreateDto.builder()
                .name("John")
                .email("john@gmail.com")
                .build();

        UserFullDto userFullDto1 = userService.create(userCreateDto1);

        assertEquals("John", userFullDto1.getName());
        assertEquals("john@gmail.com", userFullDto1.getEmail());

        UserUpdateDto userFirstUpdateDto = UserUpdateDto.builder()
                .name("John Miller")
                .build();

        UserFullDto userFullDtoFirstUpdated = userService.update(userFirstUpdateDto, userFullDto1.getId());

        assertEquals("John Miller", userFullDtoFirstUpdated.getName());
        assertEquals("john@gmail.com", userFullDtoFirstUpdated.getEmail());

        UserUpdateDto userSecondUpdateDto = UserUpdateDto.builder()
                .email("miller@gmail.com")
                .build();

        UserFullDto userFullDtoSecondUpdated = userService.update(userSecondUpdateDto, userFullDto1.getId());
        assertEquals("John Miller", userFullDtoSecondUpdated.getName());
        assertEquals("miller@gmail.com", userFullDtoSecondUpdated.getEmail());

        assertThrows(UserNotFoundException.class, () -> userService.update(UserUpdateDto.builder().build(), -1));
    }
}