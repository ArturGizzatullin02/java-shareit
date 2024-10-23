package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {
    @Size(max = 150, message = "Название не может быть больше 100 символов при создании вещи")
    private String name;

    @Size(max = 255, message = "Описание не может быть больше 255 символов при создании вещи")
    private String description;

    private Boolean available;
}
