package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(int userId, Item item);

    Collection<Item> getItemsOfOwner(int userId);

    Optional<Item> get(int id);

    Item update(int userId, Item item, int itemId);

    Collection<Item> search(String text);
}
