package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private UserFullDto userFullDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private User user;

    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        userFullDto = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();

        userCreateDto = UserCreateDto.builder()
                .name("John")
                .email("john@gmail.com")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .email("john_williams@gmail.com")
                .build();
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userFullDto));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userFullDto))));
        verify(userService).getAll();
    }

    @Test
    void getUser() throws Exception {
        when(userService.getById(userId)).thenReturn(userFullDto);

        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userFullDto)));
        verify(userService).getById(userId);
    }

    @Test
    void createUser() throws Exception {
        when(userService.create(userCreateDto)).thenReturn(userFullDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userFullDto)));
        verify(userService).create(userCreateDto);
    }

    @Test
    void updateUser() throws Exception {
        UserFullDto userFullDtoUpdated = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john_williams@gmail.com")
                .build();
        when(userService.update(userUpdateDto, userId)).thenReturn(userFullDtoUpdated);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userFullDtoUpdated)));
        verify(userService).update(userUpdateDto, userId);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService).delete(userId);
    }
}