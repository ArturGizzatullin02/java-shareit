package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestCreateDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 500, message = "Описание запроса не должно быть больше 500 символов")
    private String description;

    @FutureOrPresent(message = "Дата создания запроса не может быть в прошлом")
    private LocalDateTime created;

}
