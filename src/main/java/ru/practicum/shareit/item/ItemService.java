package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    Item create(int userId, ItemCreateDto item);

    Item update(int userId, ItemUpdateDto item, int itemId);

    Item get(int itemId);

    Collection<Item> getItemsOfOwner(int userId);

    Collection<Item> search(String text);
}
