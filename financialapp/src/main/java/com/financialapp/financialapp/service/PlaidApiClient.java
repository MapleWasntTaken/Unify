package com.financialapp.financialapp.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.financialapp.api.PlaidApiException;
import com.financialapp.financialapp.config.PlaidEnvironment;
import com.financialapp.financialapp.config.PlaidProperties;

@Component
public class PlaidApiClient {
    private static final Logger log = LoggerFactory.getLogger(PlaidApiClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlaidProperties properties;

    public PlaidApiClient(RestTemplate restTemplate, ObjectMapper objectMapper, PlaidProperties properties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public JsonNode post(String operation, String path, Map<String, ?> requestBody) {
        Map<String, Object> body = new HashMap<>(requestBody);
        body.put("client_id", properties.clientId());
        body.put("secret", properties.secret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String endpoint = properties.endpoint(path);

        log.info("Plaid request started operation={} environment={}", operation, properties.environment());
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
            JsonNode result = objectMapper.readTree(response.getBody());
            log.info("Plaid request succeeded operation={} environment={} durationMs={}",
                operation, properties.environment(), elapsedMillis(startedAt));
            return result;
        } catch (HttpStatusCodeException exception) {
            throw fromPlaidError(operation, exception);
        } catch (PlaidApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new PlaidApiException(
                HttpStatus.BAD_GATEWAY, "UPSTREAM_ERROR", "PLAID_UNAVAILABLE",
                "Plaid could not complete the request.", null, operation, exception
            );
        }
    }

    public void validateTokenEnvironment(String operation, String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("A Plaid token is required");
        }
        boolean sandboxToken = token.contains("-sandbox-");
        boolean productionToken = token.contains("-production-");
        boolean mismatch = properties.environment() == PlaidEnvironment.PROD && sandboxToken
            || properties.environment() == PlaidEnvironment.SANDBOX && productionToken;
        if (mismatch) {
            throw new PlaidApiException(
                HttpStatus.BAD_GATEWAY, "CONFIGURATION_ERROR", "PLAID_ENVIRONMENT_MISMATCH",
                "Plaid token environment mismatch.", null, operation, null
            );
        }
    }

    public PlaidProperties properties() {
        return properties;
    }

    private PlaidApiException fromPlaidError(String operation, HttpStatusCodeException exception) {
        try {
            JsonNode error = objectMapper.readTree(exception.getResponseBodyAsString());
            return new PlaidApiException(
                exception.getStatusCode(), text(error, "error_type"), text(error, "error_code"),
                "Plaid rejected the request.", text(error, "request_id"), operation, exception
            );
        } catch (Exception parseException) {
            return new PlaidApiException(
                exception.getStatusCode(), "UPSTREAM_ERROR", "PLAID_UNPARSEABLE_ERROR",
                "Plaid rejected the request.", null, operation, exception
            );
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
