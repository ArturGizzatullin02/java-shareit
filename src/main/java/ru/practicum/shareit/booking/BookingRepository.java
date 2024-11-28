package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerId(Long id);

    Collection<Booking> findAllByItemId(Long id);

    List<Booking> findAllByItemIdIn(Collection<Long> ids);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateTime1, LocalDateTime dateTime2);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);


    Collection<Booking> findAllByBookerIdAndItemIn(Long userId, Collection<Item> items);

    Collection<Booking> findAllByBookerIdAndItemInAndStartAfterOrderByStartDesc(Long userId, Collection<Item> items, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndItemInAndEndBeforeOrderByStartDesc(Long userId, Collection<Item> items, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndItemInAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, Collection<Item> items, LocalDateTime dateTime1, LocalDateTime dateTime2);

    Collection<Booking> findAllByBookerIdAndItemInAndStatusOrderByStartDesc(Long userId, Collection<Item> items, BookingStatus status);
}
