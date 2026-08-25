package com.financialapp.financialapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	"plaid.client-id=test-client-id",
	"plaid.secret=test-secret",
	"plaid.environment=SANDBOX",
	"plaid.webhook-url=https://example.test/api/plaid/webhook",
	"plaid.client-name=FinancialApp Test",
	"app.security.remember-me-key=test-remember-me-key-with-at-least-32-characters"
})
class FinancialappApplicationTests {

	@Test
	void contextLoads() {
	}

}
