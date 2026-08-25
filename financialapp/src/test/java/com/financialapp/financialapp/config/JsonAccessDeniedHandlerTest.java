package com.financialapp.financialapp.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class JsonAccessDeniedHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final JsonAccessDeniedHandler handler = new JsonAccessDeniedHandler(objectMapper);

    @Test
    void missingCsrfTokenReturnsRecognizableJsonCode() throws Exception {
        MockHttpServletResponse response = handle(new MissingCsrfTokenException("missing"));
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(json(response).get("code").asText()).isEqualTo("CSRF_TOKEN_MISSING");
    }

    @Test
    void invalidCsrfTokenReturnsRecognizableJsonCode() throws Exception {
        DefaultCsrfToken expected = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "expected");
        MockHttpServletResponse response = handle(new InvalidCsrfTokenException(expected, "actual"));
        assertThat(json(response).get("code").asText()).isEqualTo("CSRF_TOKEN_INVALID");
    }

    private MockHttpServletResponse handle(Exception exception) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.handle(
            new MockHttpServletRequest(), response,
            (org.springframework.security.access.AccessDeniedException) exception
        );
        return response;
    }

    private JsonNode json(MockHttpServletResponse response) throws Exception {
        return objectMapper.readTree(response.getContentAsString());
    }
}
