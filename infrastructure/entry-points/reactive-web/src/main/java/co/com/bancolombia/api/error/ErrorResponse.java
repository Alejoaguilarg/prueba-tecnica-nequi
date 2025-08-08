package co.com.bancolombia.api.error;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String timestamp,
        String errorCode
) {
    public ErrorResponse(int status, String error, String message, String path, String errorCode) {
        this(status, error, message, path, Instant.now().toString(), errorCode);
    }
}

