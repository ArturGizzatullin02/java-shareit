package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user, long id) {
        User userInMap = users.get(id);
        String userEmail = user.getEmail();
        String userName = user.getName();
        if (userEmail != null) {
            emails.remove(userEmail);
            userInMap.setEmail(userEmail);
            emails.add(userEmail);
        }
        if (userName != null) {
            userInMap.setName(userName);
        }
        return userInMap;
    }

    @Override
    public void delete(long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return emails.contains(email);
    }

    private long generateId() {
        return id++;
    }
}
