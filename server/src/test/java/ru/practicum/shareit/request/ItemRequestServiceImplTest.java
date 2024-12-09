package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    private Item item;
    private User user;
    private ItemRequest itemRequest;
    private ItemForRequestDto itemForRequestDto;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestCreateResponseDto itemRequestCreateResponseDto;
    private ItemRequestGetDto itemRequestGetDto;
    private Set<ItemForRequestDto> items;

    private LocalDateTime now = LocalDateTime.now();
    private Long userId = 1L;
    private Long itemId = 1L;
    private Long requestId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        item = Item.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .user(user)
                .build();

        itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("Laptop")
                .requester(user)
                .created(now)
                .build();

        itemForRequestDto = ItemForRequestDto.builder()
                .id(itemId)
                .name("Laptop")
                .userId(userId)
                .build();

        items = Set.of(itemForRequestDto);

        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Laptop")
                .created(now)
                .build();

        itemRequestCreateResponseDto = ItemRequestCreateResponseDto.builder()
                .id(requestId)
                .description("Laptop")
                .created(now)
                .build();

        itemRequestGetDto = ItemRequestGetDto.builder()
                .id(requestId)
                .description("Laptop")
                .created(now)
                .items(items)
                .build();

    }

    @Test
    @DisplayName("Создание нового запроса должно возвращать ItemRequestCreateResponseDto")
    void createItemRequest() {
        when(mapper.map(itemRequestCreateDto, ItemRequest.class)).thenReturn(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(mapper.map(itemRequest, ItemRequestCreateResponseDto.class)).thenReturn(itemRequestCreateResponseDto);

        ItemRequestCreateResponseDto result = itemRequestService.createItemRequest(itemRequestCreateDto, userId);
        assertNotNull(result);
        assertEquals(result, itemRequestCreateResponseDto);
    }

    @Test
    void getAllUserItemRequests() {
        when(userRepository.existsById(userId)).thenReturn(true);
        Collection<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        when(itemRequestRepository.findByRequesterId(userId)).thenReturn(itemRequests);
        when(mapper.map(itemRequests, new TypeToken<Collection<ItemRequestGetDto>>() {
        }.getType())).thenReturn(Collections.singletonList(itemRequestGetDto));

        Collection<ItemRequestGetDto> result = itemRequestService.getAllUserItemRequests(userId);
        assertNotNull(result);
        assertEquals(result, Collections.singletonList(itemRequestGetDto));

    }

    @Test
    void getAllItemRequests() {
        when(itemRequestRepository.findAllSortedByCreatedDesc()).thenReturn(Collections.singletonList(itemRequest));

        Collection<ItemRequest> itemRequests = Collections.singletonList(itemRequest);

        when(mapper.map(itemRequests, new TypeToken<Collection<ItemRequestGetDto>>() {
        }.getType())).thenReturn(Collections.singletonList(itemRequestGetDto));

        Collection<ItemRequestGetDto> result = itemRequestService.getAllItemRequests();
        assertNotNull(result);
        assertEquals(result, Collections.singletonList(itemRequestGetDto));
    }

    @Test
    void getItemRequest() {
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(mapper.map(itemRequest, ItemRequestGetDto.class)).thenReturn(itemRequestGetDto);

        Set<Item> itemsByRequestId = Set.of(item);

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(itemsByRequestId);
        when(mapper.map(itemsByRequestId, new TypeToken<Set<ItemForRequestDto>>() {
        }.getType())).thenReturn(Set.of(itemForRequestDto));

        ItemRequestGetDto result = itemRequestService.getItemRequest(requestId);
        assertNotNull(result);
        assertEquals(result, itemRequestGetDto);
    }
}