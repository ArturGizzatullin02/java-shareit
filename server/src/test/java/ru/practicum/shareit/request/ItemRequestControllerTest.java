package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long requestId = 1L;
    private LocalDateTime now = LocalDateTime.now();

    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestGetDto itemRequestGetDto;
    private ItemForRequestDto itemForRequestDto;
    private ItemRequestCreateResponseDto itemRequestCreateResponseDto;
    private Set<ItemForRequestDto> items;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Laptop")
                .created(now)
                .build();

        itemForRequestDto = ItemForRequestDto.builder()
                .id(itemId)
                .name("Laptop")
                .userId(userId)
                .build();

        items = Set.of(itemForRequestDto);

        itemRequestGetDto = ItemRequestGetDto.builder()
                .id(requestId)
                .description("Laptop")
                .created(now)
                .items(items)
                .build();

        itemRequestCreateResponseDto = ItemRequestCreateResponseDto.builder()
                .id(requestId)
                .description("Laptop")
                .created(now)
                .build();
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(itemRequestCreateDto, userId)).thenReturn(itemRequestCreateResponseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemRequestCreateResponseDto)));
    }

    @Test
    void getAllUserItemRequests() throws Exception {
        Set<ItemRequestGetDto> itemRequestGetDtos = Set.of(itemRequestGetDto);

        when(itemRequestService.getAllUserItemRequests(userId)).thenReturn(itemRequestGetDtos);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemRequestGetDtos)));
    }

    @Test
    void getAllItemRequests() throws Exception {
        Set<ItemRequestGetDto> itemRequestGetDtos = Set.of(itemRequestGetDto);

        when(itemRequestService.getAllItemRequests()).thenReturn(itemRequestGetDtos);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemRequestGetDtos)));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(requestId)).thenReturn(itemRequestGetDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemRequestGetDto)));
    }
}