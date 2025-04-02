package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.dto.UserFullDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingFullDtoTest {

    @Autowired
    private JacksonTester<BookingFullDto> json;


    @Test
    void testSerialize() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

        ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .id(1L)
                .name("Laptop")
                .userId(1L)
                .build();
        UserFullDto bookerDto = UserFullDto.builder()
                .id(1L)
                .name("John")
                .email("john@gmail.com")
                .build();
        BookingFullDto bookingFullDto = BookingFullDto.builder()
                .id(1L)
                .start(startDate)
                .end(endDate)
                .item(itemDto)
                .booker(bookerDto)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingFullDto> jsonContent = json.write(bookingFullDto);
        assertThat(jsonContent).hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.item.id")
                .hasJsonPath("$.item.name")
                .hasJsonPath("$.item.userId")
                .hasJsonPath("$.booker.id")
                .hasJsonPath("$.booker.name")
                .hasJsonPath("$.booker.email")
                .hasJsonPath("$.status");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .satisfies(bookingId -> assertThat(bookingId.longValue()).isEqualTo(bookingFullDto.getId()));

        String formattedStartDate = bookingFullDto.getStart().toString().substring(0, 23);
        String formattedEndDate = bookingFullDto.getEnd().toString().substring(0, 23);

        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .satisfies(startDateStr -> assertThat(startDateStr.substring(0, 23)).isEqualTo(formattedStartDate));

        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .satisfies(endDateStr -> assertThat(endDateStr.substring(0, 23)).isEqualTo(formattedEndDate));


        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id")
                .satisfies(itemId -> assertThat(itemId.longValue()).isEqualTo(bookingFullDto.getItem().getId()));

        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name")
                .satisfies(itemName -> assertThat(itemName).isEqualTo(bookingFullDto.getItem().getName()));

        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.userId")
                .satisfies(userId -> assertThat(userId.longValue()).isEqualTo(bookingFullDto.getItem().getUserId()));

        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id")
                .satisfies(bookerId -> assertThat(bookerId.longValue()).isEqualTo(bookingFullDto.getBooker().getId()));

        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.name")
                .satisfies(bookerName -> assertThat(bookerName).isEqualTo(bookingFullDto.getBooker().getName()));

        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.email")
                .satisfies(bookerEmail -> assertThat(bookerEmail).isEqualTo(bookingFullDto.getBooker().getEmail()));

        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .satisfies(status -> assertThat(status).isEqualTo(bookingFullDto.getStatus().toString()));
    }

}