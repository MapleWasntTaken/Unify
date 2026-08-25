package com.financialapp.financialapp.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {
    @Test
    void publicTokenErrorReturnsSafeSpecificResponse() {
        PlaidApiException exception = new PlaidApiException(
            HttpStatus.BAD_REQUEST,
            "INVALID_INPUT",
            "INVALID_PUBLIC_TOKEN",
            "upstream message containing public-sandbox-secret-value",
            "plaid-request-id",
            "public-token-exchange",
            null
        );

        ResponseEntity<ApiError> response = new GlobalExceptionHandler().handlePlaid(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("PLAID_PUBLIC_TOKEN_INVALID");
        assertThat(response.getBody().message()).doesNotContain("public-sandbox-secret-value");
    }
}
