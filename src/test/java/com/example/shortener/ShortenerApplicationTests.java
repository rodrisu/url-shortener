package com.example.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ShortenerApplicationTests {

	@Test
	void contextLoads() {
		ShortenerApplication.main(new String[]{});
		assertTrue(true, "Application context loaded successfully.");
	}

}
