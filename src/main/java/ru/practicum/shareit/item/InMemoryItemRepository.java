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
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Item>> usersItems = new HashMap<>();
    private int id = 1;

    @Override
    public Item save(int userId, Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        usersItems.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Collection<Item> getItemsOfOwner(int userId) {
        return usersItems.get(userId);
    }

    @Override
    public Optional<Item> get(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item update(int userId, Item item, int itemId) {
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
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> (item.getDescription().contains(text) || item.getName().contains(text))
                        && item.getAvailable())
                .toList();
    }

    private int generateId() {
        return id++;
    }
}
