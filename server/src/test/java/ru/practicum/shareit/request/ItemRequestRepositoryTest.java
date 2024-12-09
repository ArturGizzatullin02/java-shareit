package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllSortedByCreatedDesc() {
        User user = new User(1L, "john", "john@gmail.com");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 12, 0);

        ItemRequest itemRequest1 = new ItemRequest(1L, "macbook", user, now);
        ItemRequest itemRequest2 = new ItemRequest(2L, "macbook", user, now.minusDays(1));
        ItemRequest itemRequest3 = new ItemRequest(3L, "macbook", user, now.plusDays(1));

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);

        Collection<ItemRequest> expectedItemRequests = List.of(itemRequest3, itemRequest1, itemRequest2);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllSortedByCreatedDesc();
        assertFalse(itemRequests.isEmpty());
        assertEquals(expectedItemRequests, itemRequests);
    }

    @Test
    void findByRequesterId_WhenEmpty() {
        Collection<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(1L);
        assertTrue(itemRequests.isEmpty());
    }

    @Test
    void findByRequesterId_WhenIsNotEmpty() {
        User user = new User(null, "john", "john@gmail.com");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 12, 0);

        ItemRequest itemRequest = new ItemRequest(null, "macbook", user, now);

        itemRequestRepository.save(itemRequest);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(user.getId());
        assertFalse(itemRequests.isEmpty());
    }
}