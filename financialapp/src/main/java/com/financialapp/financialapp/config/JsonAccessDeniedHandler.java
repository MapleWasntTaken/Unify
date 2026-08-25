package com.financialapp.financialapp.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.financialapp.api.ApiError;
import com.financialapp.financialapp.api.RequestCorrelationFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException exception
    ) throws IOException, ServletException {
        String code;
        String message;
        if (exception instanceof MissingCsrfTokenException) {
            code = "CSRF_TOKEN_MISSING";
            message = "The security token was missing. Refresh it and retry the request.";
        } else if (exception instanceof InvalidCsrfTokenException) {
            code = "CSRF_TOKEN_INVALID";
            message = "The security session changed. Refresh the token and retry the request.";
        } else {
            code = "ACCESS_DENIED";
            message = "You do not have permission to perform this action.";
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiError.of(
            403, code, message, RequestCorrelationFilter.currentRequestId()
        ));
    }
}
