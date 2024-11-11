package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemFullDto create(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody ItemCreateDto item) {
        log.info("[ITEM CONTROLLER] Starting creating item {} for user with id {}", item, userId);
        ItemFullDto itemSaved = itemService.create(userId, item);
        log.info("[ITEM CONTROLLER] ItemFullDto {} for user with id {} created", itemSaved.getId(), userId);
        return itemSaved;
    }

    @GetMapping
    public Collection<ItemFullDto> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("[ITEM CONTROLLER] Getting items of user with id {}", userId);
        Collection<ItemFullDto> result = itemService.getItemsOfOwner(userId);
        log.info("[ITEM CONTROLLER] Getting items of user with id {} finished", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemFullDto get(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable(name = "itemId") long itemId) {
        log.info("[ITEM CONTROLLER] Getting item with id {}", itemId);
        ItemFullDto result = itemService.get(itemId);
        log.info("[ITEM CONTROLLER] Getting item with id {} finished", itemId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemFullDto update(@RequestHeader(name = USER_ID_HEADER) long userId,
                              @PathVariable(name = "itemId") long itemId, @Valid @RequestBody ItemUpdateDto item) {
        log.info("[ITEM CONTROLLER] Starting updating item {} with id {} for user with id {}", item, itemId, userId);
        ItemFullDto result = itemService.update(userId, item, itemId);
        log.info("[ITEM CONTROLLER] Item {} for user with id {} updated", result.getId(), userId);
        return result;
    }

    @GetMapping("/search")
    public Collection<ItemFullDto> search(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestParam(name = "text") String text) {
        log.info("[ITEM CONTROLLER] Searching items with text {} in name or description", text);
        Collection<ItemFullDto> result = itemService.search(text);
        log.info("[ITEM CONTROLLER] Searching items with text {} finished", text);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable(name = "itemId") long itemId,
                                     @Valid @RequestBody CommentCreateDto comment) {
        log.info("[ITEM CONTROLLER] Adding comment {} to item with id {} for user with id {}", comment, itemId, userId);
        CommentFullDto result = itemService.createComment(itemId, userId, comment);
        log.info("[ITEM CONTROLLER] Comment {} added to item with id {}", comment, itemId);
        return result;
    }
}
