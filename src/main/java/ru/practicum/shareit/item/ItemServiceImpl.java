package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public Item create(int userId, ItemCreateDto item) {
        log.info("[ITEM Service] Starting creating item {} for user with id {}", item, userId);
        Item itemForCreate = mapper.map(item, Item.class);
        itemForCreate.setOwner(userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found")));
        Item itemSaved = repository.save(userId, itemForCreate);
        log.info("[ITEM Service] Item {} for user with id {} created", itemSaved.getId(), userId);
        return itemSaved;
    }

    public Item update(int userId, ItemUpdateDto item, int itemId) {
        log.info("[ITEM Service] Starting updating item {} for user with id {}", item, userId);
        Item itemForUpdate = mapper.map(item, Item.class);
        Item itemFromRepository = repository.get(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found",
                        itemForUpdate.getId())));
        User userFromRepository = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        if (!itemFromRepository.getOwner().equals(userFromRepository)) {
            throw new PermissionDeniedException(String.format("User with id %d doesn't own item with id %d", userId,
                    itemId));
        }
        return repository.update(userId, itemForUpdate, itemId);
    }

    public Item get(int itemId) {
        log.info("[ITEM Service] Starting getting item with id {}", itemId);
        Item result = repository.get(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId)));
        log.info("[ITEM Service] Item with id {} received", itemId);
        return result;
    }

    public Collection<Item> getItemsOfOwner(int userId) {
        log.info("[ITEM Service] Starting getting items of user with id {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User with id %d not found", userId));
        }
        Collection<Item> result = repository.getItemsOfOwner(userId);
        log.info("[ITEM Service] Items of user with id {} received", userId);
        return result;
    }

    public Collection<Item> search(String text) {
        log.info("[ITEM Service] Starting searching items by text {}", text);
        Collection<Item> result = repository.search(text);
        log.info("[ITEM Service] Items by text {} received", text);
        return result;
    }
}
