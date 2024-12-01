package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {
    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
