package com.financialapp.financialapp.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;

class RequestCorrelationFilterTest {
    @Test
    void runsBeforeSpringSecurity() {
        assertThat(OrderUtils.getOrder(RequestCorrelationFilter.class))
            .isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }
}
