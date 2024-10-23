package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(long userId, Item item);

    Collection<Item> getItemsOfOwner(long userId);

    Optional<Item> get(long id);

    Item update(long userId, Item item, long itemId);

    Collection<Item> search(String text);
}
