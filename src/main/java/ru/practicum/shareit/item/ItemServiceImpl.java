package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.CommentForNotStartedBookingException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ModelMapper mapper;

    @Override
    public ItemFullDto create(long userId, ItemCreateDto item) {
        log.info("Starting creating item {} for user with id {}", item, userId);
        Item itemForCreate = mapper.map(item, Item.class);
        itemForCreate.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found")));
        Item itemSaved = itemRepository.save(itemForCreate);
        log.info("Item {} for user with id {} created", itemSaved.getId(), userId);
        return mapper.map(itemSaved, ItemFullDto.class);
    }

    @Override
    public ItemFullDto update(long userId, ItemUpdateDto item, long itemId) {
        log.info("Starting updating item {} for user with id {}", item, userId);
        Item itemForUpdate = mapper.map(item, Item.class);
        Item itemFromRepository = itemRepository.findById(itemId)
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
        log.info("Item with id {} updated", itemForUpdate.getId());
        return mapper.map(itemForUpdate, ItemFullDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemFullDto get(long itemId) {
        log.info("Starting getting item with id {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId)));

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        Collection<CommentShortDto> commentDtos = mapper.map(comments, new TypeToken<Collection<CommentShortDto>>() {
        }.getType());
        ItemFullDto itemDto = mapper.map(item, ItemFullDto.class);
        itemDto.setComments(commentDtos);

        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings = bookingRepository.findAllByItemId(itemId);

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd));
        BookingShortDto lastBookingDto = mapper.map(lastBooking, BookingShortDto.class);
        itemDto.setLastBooking(lastBookingDto);

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart));
        BookingShortDto nextBookingDto = mapper.map(nextBooking, BookingShortDto.class);
        itemDto.setNextBooking(nextBookingDto);

        log.info("Item with id {} received", item.getId());
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemFullDto> getItemsOfOwner(long userId) {
        log.info("Starting getting items of user with id {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User with id %d not found", userId));
        }

        Collection<Item> itemsOfOwner = itemRepository.findAllByUserId(userId);
        Collection<ItemFullDto> itemDtos = mapper.map(itemsOfOwner, new TypeToken<Collection<ItemFullDto>>() {
        }.getType());
        List<Long> itemIds = itemsOfOwner.stream()
                .map(Item::getId)
                .toList();


        Collection<Comment> comments = commentRepository.findAllByItemIn(itemsOfOwner);
        Collection<CommentShortDto> commentsDtos = mapper.map(comments, new TypeToken<Collection<CommentShortDto>>() {
        }.getType());


        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);
        Map<Long, List<Booking>> bookingsByItemIds = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        for (ItemFullDto item : itemDtos) {
            Optional<Booking> lastBooking = bookingsByItemIds.get(item.getId()).stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .max(Comparator.comparing(Booking::getEnd));
            BookingShortDto lastBookingDto = mapper.map(lastBooking, BookingShortDto.class);
            item.setLastBooking(lastBookingDto);

            Optional<Booking> nextBooking = bookingsByItemIds.get(item.getId()).stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            BookingShortDto nextBookingDto = mapper.map(nextBooking, BookingShortDto.class);
            item.setNextBooking(nextBookingDto);

            item.setComments(commentsDtos);
        }
        log.info("Items of user with id {} received", userId);
        return itemDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemFullDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Starting searching items by text {}", text);
        Collection<Item> result = itemRepository
                .searchByDescriptionOrName(text);
        log.info("Items by text {} received", text);
        return mapper.map(result, new TypeToken<Collection<ItemFullDto>>() {
        }.getType());
    }

    @Override
    public CommentFullDto createComment(long itemId, long userId, CommentCreateDto comment) {
        log.info("Starting creating comment {} for item with id {} and user with id {}", comment,
                itemId, userId);
        Comment commentForCreate = mapper.map(comment, Comment.class);
        commentForCreate.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId))));
        commentForCreate.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId))));
        Collection<Booking> allByBookerId = bookingRepository.findAllByBookerId(userId);
        LocalDateTime now = LocalDateTime.now();

        boolean isItemReallyBookedByUser = false;

        for (Booking booking : allByBookerId) {
            if (booking.getItem().getId() == itemId) {
                if (booking.getStart().isAfter(now)) {
                    throw new CommentForNotStartedBookingException(String
                            .format("Booking for item with id %d by user with id %d is not started yet", itemId, userId));
                }
                isItemReallyBookedByUser = true;
                break;
            }
        }
        if (isItemReallyBookedByUser) {
            Comment savedComment = commentRepository.save(commentForCreate);
            log.info("Comment {} created", savedComment);
            CommentFullDto savedCommentDto = mapper.map(savedComment, CommentFullDto.class);
            savedCommentDto.setCreated(now);
            savedCommentDto.setAuthorName(savedComment.getAuthor().getName());
            return savedCommentDto;
        } else {
            throw new PermissionDeniedException(String.format("Item with id %d not rented by user with id %d",
                    itemId, userId));
        }
    }
}
