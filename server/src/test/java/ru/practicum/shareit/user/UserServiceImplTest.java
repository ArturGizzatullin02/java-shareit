package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ModelMapper mapper;

    @Mock
    private UserRepository repository;

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
    void getAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(user));
        when(mapper.map(anyCollection(), eq(new TypeToken<Collection<UserFullDto>>() {
        }.getType()))).thenReturn(Collections.singletonList(userFullDto));

        Collection<UserFullDto> result = userService.getAll();
        assertNotNull(result);
        assertEquals(Collections.singletonList(userFullDto), result);
    }

    @Test
    void getById() {
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(user, UserFullDto.class)).thenReturn(userFullDto);

        UserFullDto result = userService.getById(userId);
        assertNotNull(result);
        assertEquals(userFullDto, result);
    }

    @Test
    void create() {
        when(mapper.map(userCreateDto, User.class)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.map(user, UserFullDto.class)).thenReturn(userFullDto);

        UserFullDto result = userService.create(userCreateDto);
        assertNotNull(result);
        assertEquals(userFullDto, result);
    }

    @Test
    void update() {

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .email("john_williams@gmail.com")
                .build();

        UserFullDto userFullDtoUpdated = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john_williams@gmail.com")
                .build();

        when(repository.save(user)).thenReturn(user);
        when(mapper.map(user, UserFullDto.class)).thenReturn(userFullDtoUpdated);

        UserFullDto result = userService.update(userUpdateDto, userId);
        assertNotNull(result);
        assertEquals(userFullDtoUpdated.getEmail(), result.getEmail());
        verify(repository, times(1)).save(user);
    }

    @Test
    void delete() {
        when(repository.existsById(userId)).thenReturn(true);
        userService.delete(userId);
        verify(repository).deleteById(userId);
    }
}