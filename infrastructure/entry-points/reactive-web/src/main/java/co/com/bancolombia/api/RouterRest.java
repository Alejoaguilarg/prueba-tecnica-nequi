package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(POST("/api/branches"), handler::addBranch)
                .andRoute(POST("/api/products"), handler::addProduct)
                .andRoute(DELETE("/api/products/{id}"), handler::deleteProduct)
                .andRoute(PATCH("/api/products/{id}/stock"), handler::updateProductStock)
                .andRoute(PATCH("/api/products/{id}/name"), handler::updateProductName)
                .andRoute(PATCH("/api/branches/{id}/name"), handler::updateBranchName)
                .andRoute(PATCH("/api/franchises/{id}/name"), handler::updateFranchiseName)
                .andRoute(GET("api/franchises/{id}/max-stock-products"), handler::getMaxStockProductsByFranchiseId);
    }
}
