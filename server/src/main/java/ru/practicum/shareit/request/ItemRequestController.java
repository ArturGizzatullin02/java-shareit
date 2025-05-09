package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestCreateResponseDto createItemRequest(@RequestHeader(USER_ID_HEADER) long userId, @RequestBody ItemRequestCreateDto itemRequest) {
        log.info("Creating item request {} started", itemRequest);
        ItemRequestCreateResponseDto result = itemRequestService.createItemRequest(itemRequest, userId);
        log.info("Creating item request {} finished", result);
        return result;
    }

    @GetMapping
    public Collection<ItemRequestGetDto> getAllUserItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Getting all item requests for user {} started", userId);
        Collection<ItemRequestGetDto> result = itemRequestService.getAllUserItemRequests(userId);
        log.info("Getting all item requests for user {} finished", userId);
        return result;
    }

    @GetMapping("/all")
    public Collection<ItemRequestGetDto> getAllItemRequests() {
        log.info("Getting all item requests started");
        Collection<ItemRequestGetDto> result = itemRequestService.getAllItemRequests();
        log.info("Getting all item requests finished");
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestGetDto getItemRequest(@PathVariable long requestId) {
        log.info("Getting item request {} started", requestId);
        ItemRequestGetDto result = itemRequestService.getItemRequest(requestId);
        log.info("Getting item request {} finished", result);
        return result;
    }
}
