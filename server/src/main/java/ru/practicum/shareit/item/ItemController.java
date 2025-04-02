package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemFullDto create(@RequestHeader(USER_ID_HEADER) long userId, @RequestBody ItemCreateDto item) {
        log.info("Starting creating item {} for user with id {}", item, userId);
        ItemFullDto itemSaved = itemService.create(userId, item);
        log.info("ItemFullDto {} for user with id {} created", itemSaved.getId(), userId);
        return itemSaved;
    }

    @GetMapping
    public Collection<ItemFullDto> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Getting items of user with id {}", userId);
        Collection<ItemFullDto> result = itemService.getItemsOfOwner(userId);
        log.info("Getting items of user with id {} finished", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemFullDto get(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable(name = "itemId") long itemId) {
        log.info("Getting item with id {} for user with id {}", itemId, userId);
        ItemFullDto result = itemService.get(itemId);
        log.info("Getting item with id {} for user with id {} finished", itemId, userId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemFullDto update(@RequestHeader(name = USER_ID_HEADER) long userId,
                              @PathVariable(name = "itemId") long itemId, @RequestBody ItemUpdateDto item) {
        log.info("Starting updating with id {} for user with id {}", itemId, userId);
        ItemFullDto result = itemService.update(userId, item, itemId);
        log.info("Item {} for user with id {} updated", result.getId(), userId);
        return result;
    }

    @GetMapping("/search")
    public Collection<ItemFullDto> search(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestParam(name = "text") String text) {
        log.info("Searching items with text {} in name or description", text);
        Collection<ItemFullDto> result = itemService.search(text);
        log.info("Searching items with text {} finished", text);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable(name = "itemId") long itemId,
                                     @RequestBody CommentCreateDto comment) {
        log.info("Adding comment {} to item with id {} for user with id {}", comment, itemId, userId);
        CommentFullDto result = itemService.createComment(itemId, userId, comment);
        log.info("Comment {} added to item with id {}", comment, itemId);
        return result;
    }
}
