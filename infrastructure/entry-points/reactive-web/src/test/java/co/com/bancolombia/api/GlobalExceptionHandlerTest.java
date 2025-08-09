package co.com.bancolombia.api;

import co.com.bancolombia.api.error.GlobalErrorAttributes;
import co.com.bancolombia.api.error.GlobalErrorWebExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@WebFluxTest
@AutoConfigureWebTestClient
@Import({ GlobalErrorWebExceptionHandler.class, GlobalErrorWebExceptionHandlerTest.TestConfig.class })
class GlobalErrorWebExceptionHandlerTest {

    @Autowired WebTestClient client;

    @Test
    void shouldReturn400ForDecodingError() {
        client.patch()
                .uri("/test/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":\"abc\"}")     // provoca DecodingException/ServerWebInputException
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("El campo 'stock' debe ser un número entero válido.")
                .jsonPath("$.path").isEqualTo("/test/stock")
                .jsonPath("$.errorCode").isEqualTo("INVALID_INPUT");
    }

    @Test
    void shouldReturn500ForUnhandledException() {
        client.get()
                .uri("/test/boom")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.path").isEqualTo("/test/boom")
                .jsonPath("$.errorCode").isEqualTo("UNKNOWN_ERROR");
    }

    @Test
    void shouldReturn404FromDefaultBranch() {
        client.get()
                .uri("/test/notfound")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.path").isEqualTo("/test/notfound")
                .jsonPath("$.errorCode").isEqualTo("UNKNOWN_ERROR");
    }

    // ----------------- Mini config SOLO para este test -----------------
    @Configuration
    static class TestConfig {

        // Usa tu clase GlobalErrorAttributes (o la que extienda DefaultErrorAttributes)
        @Bean
        GlobalErrorAttributes globalErrorAttributes() {
            return new GlobalErrorAttributes();
        }

        // *** CLAVE: el bean debe llamarse "errorWebExceptionHandler" ***
        @Bean(name = "errorWebExceptionHandler")
        GlobalErrorWebExceptionHandler errorHandler(
                GlobalErrorAttributes attrs,
                ApplicationContext ctx,
                ServerCodecConfigurer codecs) {
            return new GlobalErrorWebExceptionHandler(attrs, ctx, codecs);
        }

        // DTO para provocar DecodingException si stock es texto
        public record UpdateStockRequest(Integer stock) {}

        // Rutas mínimas para gatillar cada rama del handler
        @Bean
        RouterFunction<ServerResponse> routes() {
            return RouterFunctions.route()
                    .PATCH("/test/stock", req ->
                            req.bodyToMono(UpdateStockRequest.class)
                                    .flatMap(b -> ServerResponse.ok().bodyValue(Map.of("ok", true))))
                    .GET("/test/boom", req -> { throw new RuntimeException("Kaboom"); })
                    .GET("/test/notfound", req -> Mono.error(new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.NOT_FOUND, "nope")))
                    .build();
        }
    }
}


