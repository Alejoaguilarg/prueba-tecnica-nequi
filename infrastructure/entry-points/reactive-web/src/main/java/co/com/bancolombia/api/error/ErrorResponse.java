package co.com.bancolombia.api.error;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String timestamp,
        String errorCode,
        Map<String, Object> details
) {
    public ErrorResponse(int status, String error, String message, String path, String errorCode) {
        this(status, error, message, path, Instant.now().toString(), errorCode, Map.of());
    }
}

