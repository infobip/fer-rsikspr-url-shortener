package com.infobip.fer.course.shortener.recordingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/clean_test_data.sql")
class RecordingServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
