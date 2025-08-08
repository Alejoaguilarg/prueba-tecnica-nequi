package co.com.bancolombia.model.ex;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final String errorCode;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
