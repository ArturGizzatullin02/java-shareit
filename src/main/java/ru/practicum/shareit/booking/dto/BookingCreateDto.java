package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreateDto {

    @NotNull(message = "Необходимо указать id вещи")
    private Long itemId;

    @NotNull(message = "Необходимо указать старт бронирования")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать конец бронирования")
    private LocalDateTime end;
}
