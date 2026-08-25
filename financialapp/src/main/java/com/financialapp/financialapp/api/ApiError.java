package com.financialapp.financialapp.api;

import java.time.Instant;

public record ApiError(
    Instant timestamp,
    int status,
    String code,
    String message,
    String requestId
) {
    public static ApiError of(int status, String code, String message, String requestId) {
        return new ApiError(Instant.now(), status, code, message, requestId);
    }
}
