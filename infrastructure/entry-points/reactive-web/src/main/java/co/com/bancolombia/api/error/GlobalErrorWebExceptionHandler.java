package co.com.bancolombia.api.error;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
        Map<String, Object> m = getErrorAttributes(request, org.springframework.boot.web.error.ErrorAttributeOptions.defaults());

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
