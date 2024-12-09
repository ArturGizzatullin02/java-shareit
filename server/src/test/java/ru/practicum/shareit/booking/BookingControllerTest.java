package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long bookingId = 1L;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

    private User user;
    private Item item;
    private BookingCreateDto bookingCreateDto;
    private Booking booking;

    private ItemForRequestDto itemForRequestDto;
    private UserFullDto userFullDto;
    private BookingFullDto bookingFullDto;

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

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(startDate)
                .end(endDate)
                .build();


        booking = Booking.builder()
                .id(bookingId)
                .start(startDate)
                .end(endDate)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        itemForRequestDto = ItemForRequestDto.builder()
                .id(itemId)
                .name("Laptop")
                .userId(userId)
                .build();

        userFullDto = UserFullDto.builder()
                .id(userId)
                .name("John")
                .email("john@gmail.com")
                .build();


        bookingFullDto = BookingFullDto.builder()
                .id(bookingId)
                .start(startDate)
                .end(endDate)
                .item(itemForRequestDto)
                .booker(userFullDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(userId, bookingCreateDto)).thenReturn(bookingFullDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"itemId\":1,\"start\":\"%s\",\"end\":\"%s\"}", startDate, endDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // TODO verify на сервис
    }

    @Test
    void approve() throws Exception {
        BookingFullDto bookingFullDtoApproved = BookingFullDto.builder()
                .id(bookingId)
                .start(startDate)
                .end(endDate)
                .item(itemForRequestDto)
                .booker(userFullDto)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approve(userId, bookingId, true)).thenReturn(bookingFullDtoApproved);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getWhenSuccess() throws Exception {
        when(bookingService.get(userId, bookingId)).thenReturn(bookingFullDto);

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getAllByUserIdWithState() throws Exception {
        String state = "all";

        when(bookingService.getAllByUserIdWithState(userId, BookingStateParameter.from(state))).thenReturn(Collections.singletonList(bookingFullDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService).getAllByUserIdWithState(userId, BookingStateParameter.from(state));
    }

    @Test
    void getAllForUserItemsWithState() throws Exception {
        String state = "all";

        when(bookingService.getAllForUserItemsWithState(userId, BookingStateParameter.from(state))).thenReturn(Collections.singletonList(bookingFullDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService).getAllForUserItemsWithState(userId, BookingStateParameter.from(state));
    }
}