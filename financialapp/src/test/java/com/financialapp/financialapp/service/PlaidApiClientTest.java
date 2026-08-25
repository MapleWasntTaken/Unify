package com.financialapp.financialapp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.financialapp.api.PlaidApiException;
import com.financialapp.financialapp.config.PlaidEnvironment;
import com.financialapp.financialapp.config.PlaidProperties;

class PlaidApiClientTest {
    @Test
    void rejectsSandboxTokenWhenConfiguredForProduction() {
        PlaidApiClient client = client(PlaidEnvironment.PROD);
        assertThatThrownBy(() -> client.validateTokenEnvironment(
            "public-token-exchange", "public-sandbox-redacted"
        ))
            .isInstanceOf(PlaidApiException.class)
            .extracting("errorCode")
            .isEqualTo("PLAID_ENVIRONMENT_MISMATCH");
    }

    @Test
    void acceptsSandboxTokenInSandbox() {
        client(PlaidEnvironment.SANDBOX).validateTokenEnvironment(
            "public-token-exchange", "public-sandbox-redacted"
        );
    }

    @Test
    void productionConfigurationControlsTheOutboundUrl() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://production.plaid.com/accounts/get"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{\"accounts\":[]}", MediaType.APPLICATION_JSON));

        PlaidApiClient client = new PlaidApiClient(
            restTemplate, new ObjectMapper(), properties(PlaidEnvironment.PROD)
        );
        client.post("accounts-get", "/accounts/get", java.util.Map.of("access_token", "access-production-redacted"));
        server.verify();
    }

    private static PlaidApiClient client(PlaidEnvironment environment) {
        return new PlaidApiClient(new RestTemplate(), new ObjectMapper(), properties(environment));
    }

    private static PlaidProperties properties(PlaidEnvironment environment) {
        return new PlaidProperties(
            "client-id", "secret", environment,
            URI.create("https://example.test/webhook"), "FinancialApp"
        );
    }
}
