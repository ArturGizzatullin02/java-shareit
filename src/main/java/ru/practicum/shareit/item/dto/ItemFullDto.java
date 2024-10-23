package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemFullDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
