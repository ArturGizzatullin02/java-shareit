package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemFullDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private Collection<CommentDto> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentDto {
        private Long id;
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BookingDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
