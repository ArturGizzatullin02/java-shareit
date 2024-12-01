package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody ItemCreateDto item) {
        log.info("Starting creating item {} for user with id {}", item, userId);
        ResponseEntity<Object> itemSaved = itemClient.create(userId, item);
        log.info("Item for user with id {} created", userId);
        return itemSaved;
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Getting items of user with id {}", userId);
        ResponseEntity<Object> result = itemClient.getItemsOfOwner(userId);
        log.info("Getting items of user with id {} finished", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable(name = "itemId") long itemId) {
        log.info("Getting item with id {} for user with id {}", itemId, userId);
        ResponseEntity<Object> result = itemClient.get(itemId);
        log.info("Getting item with id {} for user with id {} finished", itemId, userId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(name = USER_ID_HEADER) long userId,
                                         @PathVariable(name = "itemId") long itemId, @Valid @RequestBody ItemUpdateDto item) {
        log.info("Starting updating with id {} for user with id {}", itemId, userId);
        ResponseEntity<Object> result = itemClient.update(userId, item, itemId);
        log.info("Item for user with id {} updated", userId);
        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam(name = "text") String text) {
        log.info("Searching items with text {} in name or description", text);
        ResponseEntity<Object> result = itemClient.search(text, userId);
        log.info("Searching items with text {} finished", text);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable(name = "itemId") long itemId,
                                             @Valid @RequestBody CommentCreateDto comment) {
        log.info("Adding comment {} to item with id {} for user with id {}", comment, itemId, userId);
        ResponseEntity<Object> result = itemClient.createComment(itemId, userId, comment);
        log.info("Comment {} added to item with id {}", comment, itemId);
        return result;
    }
}
