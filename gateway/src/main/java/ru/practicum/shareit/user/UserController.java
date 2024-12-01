package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        log.info("Started fetching all users");
        ResponseEntity<Object> result = userClient.getAll();
        log.info("Finished fetching all users");
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(name = "id") long id) {
        log.info("Started fetching user with id: {}", id);
        ResponseEntity<Object> result = userClient.getById(id);
        log.info("Finished fetching user with id: {}", id);
        return result;
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto user) {
        log.info("Started creating user: {}", user);
        ResponseEntity<Object> result = userClient.create(user);
        log.info("Finished creating user {}", user);
        return result;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateDto user, @PathVariable(name = "id") long id) {
        log.info("Started updating user with id {}", id);
        ResponseEntity<Object> result = userClient.update(user, id);
        log.info("Finished updating user with id {}", id);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") long id) {
        log.info("Started deleting user with id: {}", id);
        userClient.delete(id);
        log.info("Finished deleting user with id: {}", id);
    }
}
