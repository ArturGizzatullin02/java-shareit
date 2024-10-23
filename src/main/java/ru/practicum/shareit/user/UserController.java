package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping()
    public Collection<UserFullDto> getUsers() {
        log.info("[USER Controller] Started fetching all users");
        Collection<UserFullDto> result = userService.getAll();
        log.info("[USER Controller] Finished fetching all users");
        return result;
    }

    @GetMapping("/{id}")
    public UserFullDto getUser(@PathVariable(name = "id") long id) {
        log.info("[USER Controller] Started fetching user with id: {}", id);
        UserFullDto result = userService.getById(id);
        log.info("[USER Controller] Finished fetching user with id: {}", id);
        return result;
    }

    @PostMapping()
    public UserFullDto createUser(@Validated @RequestBody UserCreateDto user) {
        log.info("[USER Controller] Started creating user: {}", user);
        UserFullDto result = userService.create(user);
        log.info("[USER Controller] Finished creating user: {}", result);
        return result;
    }

    @PatchMapping("/{id}")
    public UserFullDto updateUser(@Validated @RequestBody UserUpdateDto user, @PathVariable(name = "id") long id) {
        log.info("[USER Controller] Started updating user: {}", user);
        UserFullDto result = userService.update(user, id);
        log.info("[USER Controller] Finished updating user: {}", result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") long id) {
        log.info("[USER Controller] Started deleting user with id: {}", id);
        userService.delete(id);
        log.info("[USER Controller] Finished deleting user with id: {}", id);
    }
}
