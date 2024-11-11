package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CommentForNotStartedBookingException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public ItemFullDto create(long userId, ItemCreateDto item) {
        log.info("[ITEM Service] Starting creating item {} for user with id {}", item, userId);
        Item itemForCreate = mapper.map(item, Item.class);
        itemForCreate.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found")));
        Item itemSaved = repository.save(itemForCreate);
        log.info("[ITEM Service] Item {} for user with id {} created", itemSaved.getId(), userId);
        return mapper.map(itemSaved, ItemFullDto.class);
    }

    @Override
    @Transactional
    public ItemFullDto update(long userId, ItemUpdateDto item, long itemId) {
        log.info("[ITEM Service] Starting updating item {} for user with id {}", item, userId);
        Item itemForUpdate = mapper.map(item, Item.class);
        Item itemFromRepository = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found",
                        itemId)));
        User userFromRepository = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        if (!itemFromRepository.getUser().equals(userFromRepository)) {
            throw new PermissionDeniedException(String.format("User with id %d doesn't own item with id %d", userId,
                    itemId));
        }
        itemForUpdate.setUser(userFromRepository);
        itemForUpdate.setId(itemId);

        if (itemForUpdate.getName() == null) {
            itemForUpdate.setName(itemFromRepository.getName());
        }
        if (itemForUpdate.getDescription() == null) {
            itemForUpdate.setDescription(itemFromRepository.getDescription());
        }
        if (itemForUpdate.getAvailable() == null) {
            itemForUpdate.setAvailable(itemFromRepository.getAvailable());
        }
        itemRepository.save(itemForUpdate);
        log.info("[ITEM Service] Item {} updated", itemForUpdate);
        return mapper.map(itemForUpdate, ItemFullDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemFullDto get(long itemId) {
        log.info("[ITEM Service] Starting getting item with id {}", itemId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId)));

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        Collection<ItemFullDto.CommentDto> commentDtos = mapper.map(comments, new TypeToken<Collection<ItemFullDto.CommentDto>>() {
        }.getType());
        ItemFullDto itemDto = mapper.map(item, ItemFullDto.class);
        itemDto.setComments(commentDtos);

        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings = bookingRepository.findAllByItemId(itemId);

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd));
        ItemFullDto.BookingDto lastBookingDto = mapper.map(lastBooking, ItemFullDto.BookingDto.class);
        itemDto.setLastBooking(lastBookingDto);

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart));
        ItemFullDto.BookingDto nextBookingDto = mapper.map(nextBooking, ItemFullDto.BookingDto.class);
        itemDto.setNextBooking(nextBookingDto);

        log.info("[ITEM Service] Item {} received", item);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemFullDto> getItemsOfOwner(long userId) {
        log.info("[ITEM Service] Starting getting items of user with id {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User with id %d not found", userId));
        }

        Collection<Item> itemsOfOwner = repository.findAllByUserId(userId);
        List<Long> itemIds = itemsOfOwner.stream()
                .map(Item::getId)
                .toList();
        Map<Long, Collection<Booking>> bookingsByItemIds = new HashMap<>();
        for (Long id : itemIds) {
            Collection<Booking> bookings = bookingRepository.findAllByItemId(id);
            bookingsByItemIds.put(id, bookings);
        }

        Collection<ItemFullDto> itemDtos = mapper.map(itemsOfOwner, new TypeToken<Collection<ItemFullDto>>() {
        }.getType());
        LocalDateTime now = LocalDateTime.now();

        for (ItemFullDto item : itemDtos) {
            Optional<Booking> lastBooking = bookingsByItemIds.get(item.getId()).stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .max(Comparator.comparing(Booking::getEnd));
            ItemFullDto.BookingDto lastBookingDto = mapper.map(lastBooking, ItemFullDto.BookingDto.class);
            item.setLastBooking(lastBookingDto);

            Optional<Booking> nextBooking = bookingsByItemIds.get(item.getId()).stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            ItemFullDto.BookingDto nextBookingDto = mapper.map(nextBooking, ItemFullDto.BookingDto.class);
            item.setNextBooking(nextBookingDto);

            Collection<Comment> comments = commentRepository.findAllByItemIn(itemsOfOwner);
            Collection<ItemFullDto.CommentDto> commentsDtos = mapper.map(comments, new TypeToken<Collection<ItemFullDto.CommentDto>>() {
            }.getType());
            item.setComments(commentsDtos);
        }
        log.info("[ITEM Service] Items of user with id {} received", userId);
        return itemDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemFullDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("[ITEM Service] Starting searching items by text {}", text);
        Collection<Item> result = repository
                .searchByDescriptionOrName(text);
        log.info("[ITEM Service] Items by text {} received", text);
        return mapper.map(result, new TypeToken<Collection<ItemFullDto>>() {
        }.getType());
    }

    @Override
    @Transactional
    public CommentFullDto createComment(long itemId, long userId, CommentCreateDto comment) {
        log.info("[ITEM Service] Starting creating comment {} for item with id {} and user with id {}", comment,
                itemId, userId);
        Comment commentForCreate = mapper.map(comment, Comment.class);
        commentForCreate.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId))));
        commentForCreate.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId))));
        Collection<Booking> allByBookerId = bookingRepository.findAllByBookerId(userId);
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : allByBookerId) {
            if (booking.getItem().getId() == itemId) {
                if (booking.getStart().isAfter(now)) {
                    throw new CommentForNotStartedBookingException(String
                            .format("Booking for item with id %d by user with id %d is not started yet", itemId, userId));
                }
                Comment savedComment = commentRepository.save(commentForCreate);
                log.info("[ITEM Service] Comment {} created", savedComment);
                CommentFullDto savedCommentDto = mapper.map(savedComment, CommentFullDto.class);
                savedCommentDto.setCreated(now);
                savedCommentDto.setAuthorName(savedComment.getAuthor().getName());
                return savedCommentDto;
            }
        }
        throw new PermissionDeniedException(String.format("Item with id %d not rented by user with id %d",
                itemId, userId));
    }
}
