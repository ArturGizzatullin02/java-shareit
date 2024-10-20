package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

/**
 * Сервис для работы с пользователями
 *
 * @author Artur Gizzatullin
 * {@link UserRepository}
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final ModelMapper mapper;
    private final UserRepository repository;

    @Override
    public Collection<User> getAll() {
        log.info("[USER Service] Starting fetching all users");
        Collection<User> result = repository.getAll();
        log.info("[USER Service] Finished fetching all users");
        return result;
    }

    @Override
    public User getById(int id) {
        log.info("[USER Service] Starting fetching user by id: {}", id);
        User result = repository.getById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("[USER Service] User with id {} was fetched", id);
        return result;
    }

    @Override
    public User create(UserCreateDto userDto) {
        log.info("[USER Service] Starting creating user");
        User user = mapper.map(userDto, User.class);
        if (repository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.getEmail()));
        }
        User userSaved = repository.save(user);
        log.info("[USER Service] User created: {}", userSaved);
        return userSaved;
    }

    @Override
    public User update(UserUpdateDto userDto, int id) {
        log.info("[USER Service] Starting updating user with id: {}", id);
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", userDto.getEmail()));
        }
        User userForUpdate = mapper.map(userDto, User.class);
        User userSaved = repository.update(userForUpdate, id);
        log.info("[USER Service] User with id {} was updated", userSaved.getId());
        return userSaved;
    }

    @Override
    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        log.info("[USER Service] Starting deleting user with id: {}", id);
        repository.delete(id);
        log.info("[USER Service] User with id {} was deleted", id);
    }
}
