package co.com.bancolombia.api.error;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler
        extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(
            GlobalErrorAttributes errorAttributes,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer) {

        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        Map<String, Object> m = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        if (error instanceof DecodingException || error instanceof ServerWebInputException) {
            return ServerResponse
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse(
                            400,
                            "Bad Request",
                            "El campo 'stock' debe ser un número entero válido.",
                            request.path(),
                            "INVALID_INPUT"
                    ));
        }

        ErrorResponse body = new ErrorResponse(
                (int) m.getOrDefault("status", 500),
                (String) m.getOrDefault("error", "Internal Server Error"),
                (String) m.getOrDefault("message", "Unexpected error"),
                (String) m.getOrDefault("path", request.path()),
                (String) m.getOrDefault("errorCode", "UNKNOWN_ERROR")
        );

        return ServerResponse
                .status(HttpStatus.valueOf(body.status()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

}
