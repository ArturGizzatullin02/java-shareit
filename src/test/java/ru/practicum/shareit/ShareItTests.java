package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.config.H2JpaConfig;

@SpringBootTest(classes = {ShareItApp.class, H2JpaConfig.class})
class ShareItTests {

    @Test
    void contextLoads() {
    }

}
