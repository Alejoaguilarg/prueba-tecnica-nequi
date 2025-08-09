package co.com.bancolombia.api.error;

import co.com.bancolombia.model.ex.DomainException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    public static final String ERROR_CODE = "errorCode";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        final String path = request.path();
        final Throwable error = getError(request);

        if (error instanceof ServerWebInputException || error instanceof DecodingException) {
            return Map.of(
                    STATUS, 400,
                    ERROR, "Bad Request",
                    MESSAGE, "El campo 'stock' debe ser un número entero válido.",
                    "path", path,
                    TIMESTAMP, Instant.now().toString(),
                    ERROR_CODE, "INVALID_INPUT"
            );
        }

        if (error instanceof ResponseStatusException rse) {
            int status = rse.getStatusCode().value();
            String reason = HttpStatus.valueOf(status).getReasonPhrase();
            String message = (rse.getReason() != null) ? rse.getReason() : reason;

            return Map.of(
                    STATUS, status,
                    ERROR, reason,
                    MESSAGE, message,
                    "path", path,
                    TIMESTAMP, Instant.now().toString(),
                    ERROR_CODE, "UNKNOWN_ERROR"
            );
        }

        if (error instanceof DomainException de) {
            ExceptionMapping m = ExceptionMapping.from(de);
            return Map.of(
                    STATUS, m.getStatus(),
                    ERROR, m.getReason(),
                    MESSAGE, de.getMessage(),
                    "path", path,
                    TIMESTAMP, Instant.now().toString(),
                    ERROR_CODE, de.getErrorCode()
            );
        }

        ExceptionMapping m = ExceptionMapping.from(error);
        return Map.of(
                STATUS, m.getStatus(),
                ERROR, m.getReason(),
                MESSAGE, (error.getMessage() != null) ? error.getMessage() : "Unexpected error",
                "path", path,
                TIMESTAMP, Instant.now().toString(),
                ERROR_CODE, "UNKNOWN_ERROR"
        );
    }
}


