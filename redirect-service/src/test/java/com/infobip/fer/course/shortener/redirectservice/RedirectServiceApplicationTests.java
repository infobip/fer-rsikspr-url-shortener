package com.infobip.fer.course.shortener.redirectservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/setup_test_db.sql")
class RedirectServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
