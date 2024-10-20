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
    private final ItemServiceImpl itemService;

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") int userId, @Validated @RequestBody ItemCreateDto item) {
        log.info("[ITEM Controller] Starting creating item {} for user with id {}", item, userId);
        Item itemSaved = itemService.create(userId, item);
        log.info("[ITEM Controller] Item {} for user with id {} created", itemSaved.getId(), userId);
        return itemSaved;
    }

    @GetMapping
    public Collection<Item> getItemsOfOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("[ITEM Controller] Getting items of user with id {}", userId);
        Collection<Item> result = itemService.getItemsOfOwner(userId);
        log.info("[ITEM Controller] Getting items of user with id {} finished", userId);
        return result;
    }

    @GetMapping("/{itemId}")
    public Item get(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable(name = "itemId") int itemId) {
        log.info("[ITEM Controller] Getting item with id {}", itemId);
        Item result = itemService.get(itemId);
        log.info("[ITEM Controller] Getting item with id {} finished", itemId);
        return result;
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(name = "X-Sharer-User-Id") int userId, @PathVariable(name = "itemId") int itemId
            , @Validated @RequestBody ItemUpdateDto item) {
        log.info("[ITEM Controller] Starting updating item {} for user with id {}", item, userId);
        Item result = itemService.update(userId, item, itemId);
        log.info("[ITEM Controller] Item {} for user with id {} updated", result.getId(), userId);
        return result;
    }

    @GetMapping("/search")
    public Collection<Item> search(@RequestHeader("X-Sharer-User-Id") int userId
            , @RequestParam(name = "text") String text) {
        log.info("[ITEM Controller] Searching items with text {} in name or description", text);
        Collection<Item> result = itemService.search(text);
        log.info("[ITEM Controller] Searching items with text {} finished", text);
        return result;
    }
}
