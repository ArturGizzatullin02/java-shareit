package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {

    Collection<UserFullDto> getAll();

    UserFullDto getById(long id);

    UserFullDto create(UserCreateDto userDto);

    UserFullDto update(UserUpdateDto userDto, long id);

    void delete(long id);
}
