package com.financialapp.financialapp.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "plaid")
public record PlaidProperties(
    @NotBlank String clientId,
    @NotBlank String secret,
    @NotNull PlaidEnvironment environment,
    @NotNull URI webhookUrl,
    @NotBlank String clientName
) {
    public String endpoint(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return environment().baseUrl() + normalizedPath;
    }
}
