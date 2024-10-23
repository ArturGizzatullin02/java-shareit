package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    Optional<User> getById(long id);

    User save(User user);

    User update(User user, long id);

    void delete(long id);

    boolean existsById(long id);

    boolean existsByEmail(String email);
}
