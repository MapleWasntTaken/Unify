package com.financialapp.financialapp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PlaidApiException.class)
    public ResponseEntity<ApiError> handlePlaid(PlaidApiException exception) {
        String code = clientCode(exception.errorCode());
        HttpStatus status = clientStatus(code);
        log.warn(
            "Plaid operation failed operation={} errorType={} errorCode={} plaidRequestId={} upstreamStatus={}",
            exception.operation(), exception.errorType(), exception.errorCode(),
            exception.plaidRequestId(), exception.upstreamStatus().value()
        );
        return ResponseEntity.status(status).body(ApiError.of(
            status.value(), code, clientMessage(code), RequestCorrelationFilter.currentRequestId()
        ));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        log.info("Request validation failed exceptionType={}", exception.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(ApiError.of(
            400, "INVALID_REQUEST", "The request was invalid.", RequestCorrelationFilter.currentRequestId()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("Unhandled request failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.of(
            500, "INTERNAL_ERROR", "The request could not be completed.",
            RequestCorrelationFilter.currentRequestId()
        ));
    }

    private static String clientCode(String plaidCode) {
        if (plaidCode == null) return "PLAID_UPSTREAM_ERROR";
        return switch (plaidCode) {
            case "PLAID_ENVIRONMENT_MISMATCH", "UNAUTHORIZED_ENVIRONMENT" -> "PLAID_CONFIGURATION_ERROR";
            case "INVALID_LINK_TOKEN" -> "PLAID_LINK_TOKEN_INVALID";
            case "INVALID_PUBLIC_TOKEN" -> "PLAID_PUBLIC_TOKEN_INVALID";
            case "INVALID_ACCESS_TOKEN" -> "PLAID_ACCESS_TOKEN_INVALID";
            default -> "PLAID_UPSTREAM_ERROR";
        };
    }

    private static HttpStatus clientStatus(String code) {
        return switch (code) {
            case "PLAID_LINK_TOKEN_INVALID", "PLAID_PUBLIC_TOKEN_INVALID", "PLAID_ACCESS_TOKEN_INVALID" -> HttpStatus.CONFLICT;
            case "PLAID_CONFIGURATION_ERROR" -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.BAD_GATEWAY;
        };
    }

    private static String clientMessage(String code) {
        return switch (code) {
            case "PLAID_LINK_TOKEN_INVALID" -> "The bank connection session expired. Please try again.";
            case "PLAID_PUBLIC_TOKEN_INVALID" -> "The bank connection could not be completed. Please reconnect.";
            case "PLAID_ACCESS_TOKEN_INVALID" -> "This bank connection needs to be reconnected.";
            case "PLAID_CONFIGURATION_ERROR" -> "Bank connectivity is temporarily unavailable.";
            default -> "The bank service could not complete the request.";
        };
    }
}
