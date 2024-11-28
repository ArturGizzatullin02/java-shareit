package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreateDto {

    @NotBlank(message = "Для создания комментария необходимо ввести текст")
    @Size(max = 500, message = "Длина текста должна быть не более 500 символов")
    private String text;
}
