package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Название не может быть пустым при создании вещи")
    @Size(max = 100, message = "Название не может быть больше 100 символов при создании вещи")
    private String name;

    @NotBlank(message = "Описание не может быть пустым при создании вещи")
    @Size(max = 255, message = "Описание не может быть больше 255 символов при создании вещи")
    private String description;

    @NotNull(message = "Доступность не может быть пустой при создании вещи")
    private Boolean available;

    private Long requestId;
}
