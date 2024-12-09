package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private Long userId = 1L;
    private Long itemId = 1L;

    private ItemCreateDto itemCreateDto;
    private ItemFullDto itemFullDto;
    private UserFullDto userFullDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = ItemCreateDto.builder()
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .build();

        userFullDto = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        itemFullDto = ItemFullDto.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Air")
                .available(true)
                .owner(userFullDto)
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemService.create(userId, itemCreateDto)).thenReturn(itemFullDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        verify(itemService).create(userId, itemCreateDto);
    }

    @Test
    void getItemsOfOwner() throws Exception {
        when(itemService.getItemsOfOwner(userId)).thenReturn(Collections.singletonList(itemFullDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(itemId))
                .andExpect(jsonPath("$.[0].name").value(itemFullDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemFullDto.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(itemFullDto.getAvailable()));
        verify(itemService).getItemsOfOwner(userId);
    }

    @Test
    void getItemsOfNotFoundOwner() throws Exception {
        when(itemService.getItemsOfOwner(userId)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        verify(itemService).getItemsOfOwner(userId);
    }

    @Test
    void getWhenSuccess() throws Exception {
        when(itemService.get(itemId)).thenReturn(itemFullDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemFullDto.getName()))
                .andExpect(jsonPath("$.description").value(itemFullDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemFullDto.getAvailable()));
        verify(itemService).get(itemId);
    }

    @Test
    void update() throws Exception {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name(itemFullDto.getName())
                .description("Macbook Pro M4")
                .available(itemFullDto.getAvailable())
                .build();

        ItemFullDto itemFullDtoUpdated = ItemFullDto.builder()
                .id(itemId)
                .name("Laptop")
                .description("Macbook Pro M4")
                .available(true)
                .owner(userFullDto)
                .build();

        when(itemService.update(userId, itemUpdateDto, itemId)).thenReturn(itemFullDtoUpdated);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemFullDtoUpdated.getName()))
                .andExpect(jsonPath("$.description").value(itemFullDtoUpdated.getDescription()))
                .andExpect(jsonPath("$.available").value(itemFullDtoUpdated.getAvailable()));
        verify(itemService).update(userId, itemUpdateDto, itemId);
    }

    @Test
    void search() throws Exception {
        when(itemService.search("Laptop")).thenReturn(Collections.singletonList(itemFullDto));

        mockMvc.perform(get("/items/search?text=Laptop")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value(itemFullDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemFullDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemFullDto.getAvailable()));
        verify(itemService).search("Laptop");
    }

    @Test
    void addComment() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        CommentFullDto commentFullDto = CommentFullDto.builder()
                .id(itemId)
                .text("Comment")
                .created(LocalDateTime.now())
                .build();

        when(itemService.createComment(itemId, userId, commentCreateDto)).thenReturn(commentFullDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(commentFullDto)))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.text").value(commentFullDto.getText()))
                .andExpect(jsonPath("$.created").value(lessThanOrEqualTo(LocalDateTime.now().toString())));
        verify(itemService).createComment(itemId, userId, commentCreateDto);
    }
}