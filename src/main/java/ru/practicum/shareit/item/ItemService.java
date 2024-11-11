package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemFullDto create(long userId, ItemCreateDto item);

    ItemFullDto update(long userId, ItemUpdateDto item, long itemId);

    ItemFullDto get(long itemId);

    Collection<ItemFullDto> getItemsOfOwner(long userId);

    Collection<ItemFullDto> search(String text);

    CommentFullDto createComment(long itemId, long userId, CommentCreateDto comment);
}
