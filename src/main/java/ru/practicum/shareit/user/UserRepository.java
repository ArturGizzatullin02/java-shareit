package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    Optional<User> getById(int id);

    User save(User user);

    User update(User user, int id);

    void delete(int id);

    boolean existsById(int id);

    boolean existsByEmail(String email);
}
