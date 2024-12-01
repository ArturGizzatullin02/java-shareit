package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentShortDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.user.User;

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

    private ItemRequestGetDto request;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private Collection<CommentShortDto> comments;
}
