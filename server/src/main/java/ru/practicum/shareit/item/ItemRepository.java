package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByUserId(long userId);

    @Query("select item from Item item" +
            " where item.available = true" +
            " and (lower(item.description) like lower(concat('%', :text, '%'))" +
            " or lower(item.name) like lower(concat('%', :text, '%')))")
    Collection<Item> searchByDescriptionOrName(String text);

    Set<Item> findAllByRequestId(long requestId);
}
