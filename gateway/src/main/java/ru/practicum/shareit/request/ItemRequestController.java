package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody ItemRequestCreateDto itemRequest) {
        log.info("Creating item request {} started", itemRequest);
        ResponseEntity<Object> result = itemRequestClient.createItemRequest(itemRequest, userId);
        log.info("Creating item request {} finished", result);
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Getting all item requests for user {} started", userId);
        ResponseEntity<Object> result = itemRequestClient.getAllUserItemRequests(userId);
        log.info("Getting all item requests for user {} finished", userId);
        return result;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Getting all item requests started");
        ResponseEntity<Object> result = itemRequestClient.getAllItemRequests();
        log.info("Getting all item requests finished");
        return result;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable long requestId) {
        log.info("Getting item request {} started", requestId);
        ResponseEntity<Object> result = itemRequestClient.getItemRequest(requestId);
        log.info("Getting item request {} finished", result);
        return result;
    }
}
