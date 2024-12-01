package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestCreateResponseDto createItemRequest(ItemRequestCreateDto itemRequest, long userId);

    Collection<ItemRequestGetDto> getAllUserItemRequests(long userId);

    Collection<ItemRequestGetDto> getAllItemRequests();

    ItemRequestGetDto getItemRequest(long requestId);
}
