package com.financialapp.financialapp.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PlaidPropertiesTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void sandboxEnvironmentBuildsSandboxEndpoint() {
        PlaidProperties properties = valid(PlaidEnvironment.SANDBOX);
        assertThat(properties.endpoint("/link/token/create"))
            .isEqualTo("https://sandbox.plaid.com/link/token/create");
    }

    @Test
    void prodEnvironmentBuildsProductionEndpoint() {
        PlaidProperties properties = valid(PlaidEnvironment.PROD);
        assertThat(properties.endpoint("accounts/get"))
            .isEqualTo("https://production.plaid.com/accounts/get");
    }

    @Test
    void missingClientIdFailsValidationAndNamesProperty() {
        PlaidProperties properties = new PlaidProperties(
            "", "secret", PlaidEnvironment.SANDBOX,
            URI.create("https://example.test/webhook"), "FinancialApp"
        );
        Set<ConstraintViolation<PlaidProperties>> violations = validator.validate(properties);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("clientId");
    }

    @Test
    void missingClientIdPreventsContextStartup() {
        new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfiguration.class)
            .withPropertyValues(
                "plaid.secret=secret",
                "plaid.environment=prod",
                "plaid.webhook-url=https://example.test/webhook",
                "plaid.client-name=FinancialApp"
            )
            .run(context -> {
                assertThat(context).hasFailed();
                assertThat(context.getStartupFailure()).hasMessageContaining("plaid");
            });
    }

    private static PlaidProperties valid(PlaidEnvironment environment) {
        return new PlaidProperties(
            "client-id", "secret", environment,
            URI.create("https://example.test/webhook"), "FinancialApp"
        );
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(PlaidProperties.class)
    static class PropertiesConfiguration {}
}
