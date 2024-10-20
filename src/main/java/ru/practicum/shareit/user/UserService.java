package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();

    User getById(int id);

    User create(UserCreateDto userDto);

    User update(UserUpdateDto userDto, int id);

    void delete(int id);
}
