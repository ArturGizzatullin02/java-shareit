package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    public ItemRequestCreateResponseDto createItemRequest(ItemRequestCreateDto itemRequestDto, long userId) {
        log.info("Started creating item request: {}", itemRequestDto);
        ItemRequest itemRequest = mapper.map(itemRequestDto, ItemRequest.class);
        LocalDateTime now = LocalDateTime.now();
        itemRequest.setCreated(now);
        itemRequest.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found")));
        ItemRequest result = itemRequestRepository.save(itemRequest);
        log.info("Finished creating item request: {}", result);
        return mapper.map(result, ItemRequestCreateResponseDto.class);
    }

    @Override
    public Collection<ItemRequestGetDto> getAllUserItemRequests(long userId) {
        log.info("Started getting all item requests for user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id: " + userId + " not found");
        }
        Collection<ItemRequest> userRequests = itemRequestRepository.findByRequesterId(userId);
        Collection<ItemRequestGetDto> userRequestsDto = setRequestsResponses(userRequests);

        log.info("Finished getting all item requests for user with id: {}", userId);
        return userRequestsDto;
    }

    @Override
    public Collection<ItemRequestGetDto> getAllItemRequests() {
        log.info("Started getting all item requests");
        Collection<ItemRequest> requests = itemRequestRepository.findAllSortedByCreatedDesc();
        Collection<ItemRequestGetDto> userRequestsDto = setRequestsResponses(requests);
        log.info("Finished getting all item requests");
        return userRequestsDto;
    }

    private Collection<ItemRequestGetDto> setRequestsResponses(Collection<ItemRequest> requests) {
        Collection<ItemRequestGetDto> userRequestsDto = mapper.map(requests, new TypeToken<Collection<ItemRequestGetDto>>() {
        }.getType());

        for (ItemRequestGetDto userRequestDto : userRequestsDto) {
            Set<Item> itemsByRequestId = itemRepository.findAllByRequestId(userRequestDto.getId());

            Set<ItemForRequestDto> itemsByRequestIdDto = mapper.map(itemsByRequestId,
                    new TypeToken<Set<ItemForRequestDto>>() {
                    }.getType());

            userRequestDto.setItems(itemsByRequestIdDto);
        }
        return userRequestsDto;
    }

    @Override
    public ItemRequestGetDto getItemRequest(long requestId) {
        log.info("Started getting item request: {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Item request with id: " + requestId + " not found"));
        ItemRequestGetDto itemRequestGetDto = mapper.map(itemRequest, ItemRequestGetDto.class);
        Set<Item> itemsByRequestId = itemRepository.findAllByRequestId(requestId);
        Set<ItemForRequestDto> itemsByRequestIdDto = mapper.map(itemsByRequestId, new TypeToken<Set<ItemForRequestDto>>() {
        }.getType());
        itemRequestGetDto.setItems(itemsByRequestIdDto);

        log.info("Finished getting item request: {}", itemRequestGetDto);
        return itemRequestGetDto;
    }
}
