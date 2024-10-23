package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemFullDto create(@RequestHeader(USER_ID_HEADER) long userId, @Validated @RequestBody ItemCreateDto item) {
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
        log.info("Getting item with id {}", itemId);
        ItemFullDto result = itemService.get(itemId);
        log.info("Getting item with id {} finished", itemId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemFullDto update(@RequestHeader(name = USER_ID_HEADER) long userId, @PathVariable(name = "itemId") long itemId, @Validated @RequestBody ItemUpdateDto item) {
        log.info("Starting updating item {} for user with id {}", item, userId);
        ItemFullDto result = itemService.update(userId, item, itemId);
        log.info("ItemFullDto {} for user with id {} updated", result.getId(), userId);
        return result;
    }

    @GetMapping("/search")
    public Collection<ItemFullDto> search(@RequestHeader(USER_ID_HEADER) long userId, @RequestParam(name = "text") String text) {
        log.info("Searching items with text {} in name or description", text);
        Collection<ItemFullDto> result = itemService.search(text);
        log.info("Searching items with text {} finished", text);
        return result;
    }
}
