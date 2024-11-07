package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> usersItems = new HashMap<>();
    private long id = 1;

    @Override
    public Item save(long userId, Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        usersItems.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Collection<Item> getItemsOfOwner(long userId) {
        return usersItems.get(userId);
    }

    @Override
    public Optional<Item> get(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item update(long userId, Item item, long itemId) {
        Item itemForUpdate = items.get(itemId);
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        return itemForUpdate;
    }

    @Override
    public Collection<Item> search(String text) {
        String textToLower = text.toLowerCase();
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(textToLower) || item.getName().toLowerCase().contains(textToLower))
                        && item.getAvailable())
                .toList();
    }

    private long generateId() {
        return id++;
    }
}
