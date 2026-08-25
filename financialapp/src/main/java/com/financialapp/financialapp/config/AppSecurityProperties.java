package com.financialapp.financialapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Validated
@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
    @NotBlank @Size(min = 32) String rememberMeKey
) {}
