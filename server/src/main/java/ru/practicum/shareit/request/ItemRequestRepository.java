package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir ORDER BY ir.created DESC")
    Collection<ItemRequest> findAllSortedByCreatedDesc();

    Collection<ItemRequest> findByRequesterId(Long requesterId);
}
