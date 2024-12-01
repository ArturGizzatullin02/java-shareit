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
        log.info("Starting fetching all users");
        Collection<User> result = repository.findAll();
        log.info("Finished fetching all users");
        return mapper.map(result, new TypeToken<Collection<UserFullDto>>() {
        }.getType());
    }

    @Override
    public UserFullDto getById(long id) {
        log.info("Starting fetching user by id: {}", id);
        User result = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("User with id {} was fetched", result.getId());
        return mapper.map(result, UserFullDto.class);
    }

    @Override
    public UserFullDto create(UserCreateDto userDto) {
        log.info("Starting creating user");
        User user = mapper.map(userDto, User.class);
        User userSaved = repository.save(user);
        log.info("User created with id {}", userSaved.getId());
        return mapper.map(userSaved, UserFullDto.class);
    }

    @Override
    public UserFullDto update(UserUpdateDto userDto, long id) {
        log.info("Starting updating user with id: {}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        User userSaved = repository.save(user);
        log.info("User with id {} was updated", userSaved.getId());
        return mapper.map(userSaved, UserFullDto.class);
    }

    @Override
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        log.info("Starting deleting user with id: {}", id);
        repository.deleteById(id);
        log.info("User with id {} was deleted", id);
    }
}
