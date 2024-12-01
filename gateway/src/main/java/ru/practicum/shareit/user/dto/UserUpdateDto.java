package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Size(max = 150, message = "Имя не может превышать 50 символов")
    private String name;

    @Email(message = "Некорректный email")
    @Size(max = 150, message = "Email не может превышать 50 символов")
    private String email;
}
