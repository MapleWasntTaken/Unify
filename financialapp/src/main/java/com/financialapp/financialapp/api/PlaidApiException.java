package com.financialapp.financialapp.api;

import org.springframework.http.HttpStatusCode;

public class PlaidApiException extends RuntimeException {
    private final HttpStatusCode upstreamStatus;
    private final String errorType;
    private final String errorCode;
    private final String plaidRequestId;
    private final String operation;

    public PlaidApiException(
        HttpStatusCode upstreamStatus,
        String errorType,
        String errorCode,
        String safeMessage,
        String plaidRequestId,
        String operation,
        Throwable cause
    ) {
        super(safeMessage, cause);
        this.upstreamStatus = upstreamStatus;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.plaidRequestId = plaidRequestId;
        this.operation = operation;
    }

    public HttpStatusCode upstreamStatus() { return upstreamStatus; }
    public String errorType() { return errorType; }
    public String errorCode() { return errorCode; }
    public String plaidRequestId() { return plaidRequestId; }
    public String operation() { return operation; }
}
