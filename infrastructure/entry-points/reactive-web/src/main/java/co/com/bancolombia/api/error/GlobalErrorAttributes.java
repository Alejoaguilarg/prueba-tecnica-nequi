package co.com.bancolombia.api.error;

import co.com.bancolombia.model.ex.DomainException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        Throwable error = getError(request);
        String path = request.path();

        ExceptionMapping mapping = ExceptionMapping.from(error);

        ErrorResponse response = new ErrorResponse(
                mapping.getStatus(),
                mapping.getReason(),
                error.getMessage(),
                path,
                Instant.now().toString(),
                (error instanceof DomainException exception) ? exception.getErrorCode() : "UNKNOWN",
                Map.of()
        );

        return Map.of(
                "status", response.status(),
                "error", response.error(),
                "message", response.message(),
                "path", response.path(),
                "timestamp", response.timestamp(),
                "errorCode", response.errorCode(),
                "details", response.details()
        );
    }
}

