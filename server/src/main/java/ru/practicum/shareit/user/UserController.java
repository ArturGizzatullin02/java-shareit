package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Collection<UserFullDto> getUsers() {
        log.info("Started fetching all users");
        Collection<UserFullDto> result = userService.getAll();
        log.info("Finished fetching all users");
        return result;
    }

    @GetMapping("/{id}")
    public UserFullDto getUser(@PathVariable(name = "id") long id) {
        log.info("Started fetching user with id: {}", id);
        UserFullDto result = userService.getById(id);
        log.info("Finished fetching user with id: {}", id);
        return result;
    }

    @PostMapping()
    public UserFullDto createUser(@RequestBody UserCreateDto user) {
        log.info("Started creating user: {}", user);
        UserFullDto result = userService.create(user);
        log.info("Finished creating user with id {}", result.getId());
        return result;
    }

    @PatchMapping("/{id}")
    public UserFullDto updateUser(@RequestBody UserUpdateDto user, @PathVariable(name = "id") long id) {
        log.info("Started updating user with id {}", id);
        UserFullDto result = userService.update(user, id);
        log.info("Finished updating user with id {}", result.getId());
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") long id) {
        log.info("Started deleting user with id: {}", id);
        userService.delete(id);
        log.info("Finished deleting user with id: {}", id);
    }
}
