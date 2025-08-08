package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.usecase.AddBranchUseCase;
import co.com.bancolombia.usecase.AddProductUseCase;
import co.com.bancolombia.usecase.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.DeleteProductUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, ConfigTest.TestRouter.class, ConfigTest.UseCaseMocksConfig.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @org.springframework.context.annotation.Configuration
    static class TestRouter {
        @org.springframework.context.annotation.Bean
        org.springframework.web.reactive.function.server.RouterFunction<org.springframework.web.reactive.function.server.ServerResponse> testRouterFunction() {
            return org.springframework.web.reactive.function.server.RouterFunctions.route(
                    org.springframework.web.reactive.function.server.RequestPredicates.GET("/api/usecase/path"),
                    request -> org.springframework.web.reactive.function.server.ServerResponse.ok().build()
            );
        }
    }

    @TestConfiguration
    static class UseCaseMocksConfig {
        @Bean
        CreateFranchiseUseCase createFranchiseUseCase() { return Mockito.mock(CreateFranchiseUseCase.class); }
        @Bean
        AddBranchUseCase addBranchUseCase() { return Mockito.mock(AddBranchUseCase.class); }
        @Bean
        AddProductUseCase addProductUseCase() { return Mockito.mock(AddProductUseCase.class); }
        @Bean
        DeleteProductUseCase deleteProductUseCase() { return Mockito.mock(DeleteProductUseCase.class); }
    }
}