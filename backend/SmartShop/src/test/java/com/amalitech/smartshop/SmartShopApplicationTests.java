package com.amalitech.smartshop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for the SmartShop application.
 * Tests that the Spring context loads successfully.
 */
@SpringBootTest
@Disabled("Context load test disabled - requires PostgreSQL database")
class SmartShopApplicationTests {

	@Test
	void contextLoads() {
	}

}
