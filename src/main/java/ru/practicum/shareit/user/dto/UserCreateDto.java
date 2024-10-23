package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Имя не может быть пустым при создании пользователя")
    @Size(max = 150, message = "Имя не может превышать 50 символов")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым при создании пользователя")
    @Size(max = 150, message = "Email не может превышать 50 символов")
    private String email;
}
