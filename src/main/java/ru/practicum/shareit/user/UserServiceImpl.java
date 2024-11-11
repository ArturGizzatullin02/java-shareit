package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
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
    public Collection<UserFullDto> getAll() {
        log.info("[USER Service] Starting fetching all users");
        Collection<User> result = repository.findAll();
        log.info("[USER Service] Finished fetching all users");
        return mapper.map(result, new TypeToken<Collection<UserFullDto>>() {
        }.getType());
    }

    @Override
    public UserFullDto getById(long id) {
        log.info("[USER Service] Starting fetching user by id: {}", id);
        User result = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("[USER Service] User {} was fetched", result);
        return mapper.map(result, UserFullDto.class);
    }

    @Override
    public UserFullDto create(UserCreateDto userDto) {
        log.info("[USER Service] Starting creating user");
        User user = mapper.map(userDto, User.class);
        User userSaved = repository.save(user);
        log.info("[USER Service] User created: {}", userSaved);
        return mapper.map(userSaved, UserFullDto.class);
    }

    @Override
    public UserFullDto update(UserUpdateDto userDto, long id) {
        log.info("[USER Service] Starting updating user with id: {}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }
        User userForUpdate = mapper.map(userDto, User.class);
        userForUpdate.setId(id);
        User userSaved = repository.save(userForUpdate);
        log.info("[USER Service] User {} was updated", userSaved);
        return mapper.map(userSaved, UserFullDto.class);
    }

    @Override
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        log.info("[USER Service] Starting deleting user with id: {}", id);
        repository.deleteById(id);
        log.info("[USER Service] User with id {} was deleted", id);
    }
}
