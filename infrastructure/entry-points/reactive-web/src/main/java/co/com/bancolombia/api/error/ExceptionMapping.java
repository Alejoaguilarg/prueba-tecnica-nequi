package co.com.bancolombia.api.error;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.ex.EntityNotFoundException;
import lombok.Getter;

import java.util.Arrays;

public enum ExceptionMapping {
    ENTITY_NOT_FOUND(EntityNotFoundException.class, 404, "Not Found"),
    BUSINESS_RULE(BusinessRuleException.class, 422, "Unprocessable Entity"),
    ILLEGAL_ARGUMENT(IllegalArgumentException.class, 400, "Bad Request"),
    UNKNOWN_ERROR(Exception.class, 500, "Internal Server Error");

    private final Class<? extends Throwable> type;
    @Getter
    private final int status;
    @Getter
    private final String reason;

    ExceptionMapping(Class<? extends Throwable> type, int status, String reason) {
        this.type = type;
        this.status = status;
        this.reason = reason;
    }

    public static ExceptionMapping from(Throwable ex) {
        return Arrays.stream(values())
                .filter(m -> m.type.isAssignableFrom(ex.getClass()))
                .findFirst()
                .orElse(UNKNOWN_ERROR);
    }

}

