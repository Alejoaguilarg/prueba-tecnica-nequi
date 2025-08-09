package co.com.bancolombia.api;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.valueobjects.BranchTopProduct;
import co.com.bancolombia.usecase.branch.AddBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.product.AddProductUseCase;
import co.com.bancolombia.usecase.product.DeleteProductUseCase;
import co.com.bancolombia.usecase.product.GetMaxstockProductsUseCase;
import co.com.bancolombia.usecase.product.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.product.UpdateProductStockUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.HandlerStrategies.withDefaults;

import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    private final CreateFranchiseUseCase createFranchiseUseCase = Mockito.mock(CreateFranchiseUseCase.class);
    private final AddBranchUseCase addBranchUseCase = Mockito.mock(AddBranchUseCase.class);
    private final AddProductUseCase addProductUseCase = Mockito.mock(AddProductUseCase.class);
    private final DeleteProductUseCase deleteProductUseCase = Mockito.mock(DeleteProductUseCase.class);
    private final UpdateProductStockUseCase updateProductStockUseCase = Mockito.mock(UpdateProductStockUseCase.class);
    private final UpdateProductNameUseCase updateProductNameUseCase = Mockito.mock(UpdateProductNameUseCase.class);
    private final UpdateBranchNameUseCase updateBranchNameUseCase = Mockito.mock(UpdateBranchNameUseCase.class);
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase = Mockito.mock(UpdateFranchiseNameUseCase.class);
    private final GetMaxstockProductsUseCase getMaxstockProductsUseCase = Mockito.mock(GetMaxstockProductsUseCase.class);

    private Handler handler;
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        handler = new Handler(
                createFranchiseUseCase,
                addBranchUseCase,
                addProductUseCase,
                deleteProductUseCase,
                updateProductStockUseCase,
                updateProductNameUseCase,
                updateBranchNameUseCase,
                updateFranchiseNameUseCase,
                getMaxstockProductsUseCase
        );

        RouterFunction<ServerResponse> routes = RouterFunctions.route()
                .POST("/api/franchises", handler::createFranchise)
                .POST("/api/branches", handler::addBranch)
                .POST("/api/products", handler::addProduct)
                .DELETE("/api/products/{id}", handler::deleteProduct)
                .PATCH("/api/products/{id}/stock", handler::updateProductStock)
                .PATCH("/api/products/{id}/name", handler::updateProductName)
                .PATCH("/api/branches/{id}/name", handler::updateBranchName)
                .PATCH("/api/franchises/{id}/name", handler::updateFranchiseName)
                .GET("/api/franchises/{id}/max-stock-products", handler::getMaxStockProductsByFranchiseId)
                .build();

        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void createFranchise_ok() {
        Franchise saved = Franchise.builder().franchiseId(1L).name("Acme").build();
        when(createFranchiseUseCase.execute(any(Franchise.class))).thenReturn(Mono.just(saved));

        client.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name", "Acme"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location(URI.create("/api/franchises/").toString())
                .expectBody()
                .jsonPath("$.name").isEqualTo("Acme");
    }

    @Test
    void addBranch_ok() {
        Branch added = Branch.builder().id(10L).name("Sucursal Norte").franchiseId(1L).build();
        when(addBranchUseCase.execute(any(Branch.class))).thenReturn(Mono.just(added));

        client.post()
                .uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name","Sucursal Norte","franchiseId",1))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sucursal Norte")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void addProduct_ok() {
        Product added = Product.builder().id(100L).name("Coca-Cola").stock(50).branchId(10L).build();
        when(addProductUseCase.execute(any(Product.class))).thenReturn(Mono.just(added));

        client.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name","Coca-Cola","stock",50,"branchId",10))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Coca-Cola")
                .jsonPath("$.stock").isEqualTo(50)
                .jsonPath("$.branchId").isEqualTo(10);
    }

    @Test
    void deleteProduct_ok() {
        when(deleteProductUseCase.execute(100L)).thenReturn(Mono.empty());

        client.delete()
                .uri("/api/products/{id}", 100)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateProductStock_ok() {
        Product updated = Product.builder().id(5L).name("Pepsi").stock(77).branchId(2L).build();
        when(updateProductStockUseCase.execute(5L, 77)).thenReturn(Mono.just(updated));

        client.patch()
                .uri("/api/products/{id}/stock", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("stock",77))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Pepsi")
                .jsonPath("$.stock").isEqualTo(77)
                .jsonPath("$.branchId").isEqualTo(2);
    }

    @Test
    void updateProductName_ok() {
        Product updated = Product.builder().id(7L).name("Nueva").stock(15).branchId(3L).build();
        when(updateProductNameUseCase.execute(7L, "Nueva")).thenReturn(Mono.just(updated));

        client.patch()
                .uri("/api/products/{id}/name", 7)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name","Nueva"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nueva")
                .jsonPath("$.stock").isEqualTo(15)
                .jsonPath("$.branchId").isEqualTo(3);
    }

    @Test
    void updateBranchName_ok() {
        Branch updated = Branch.builder().id(9L).name("Centro").franchiseId(1L).build();
        when(updateBranchNameUseCase.execute(9L, "Centro")).thenReturn(Mono.just(updated));

        client.patch()
                .uri("/api/branches/{id}/name", 9)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name","Centro"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Centro")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void updateFranchiseName_ok() {
        Franchise updated = Franchise.builder().franchiseId(1L).name("Acme Plus").build();
        when(updateFranchiseNameUseCase.execute(1L, "Acme Plus")).thenReturn(Mono.just(updated));

        client.patch()
                .uri("/api/franchises/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name","Acme Plus"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Acme Plus");
    }

    @Test
    void getMaxStockProducts_ok() {
        Product top = Product.builder().id(101L).name("Coca-Cola").stock(200).branchId(11L).build();
        BranchTopProduct projection = new BranchTopProduct(11L, "Sucursal Centro", top); // usa builder si tu record lo expone

        when(getMaxstockProductsUseCase.execute(1L)).thenReturn(Flux.just(projection));

        client.get()
                .uri("/api/franchises/{id}/max-stock-products", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].branchName").isEqualTo("Sucursal Centro")
                .jsonPath("$[0].product.name").isEqualTo("Coca-Cola")
                .jsonPath("$[0].product.stock").isEqualTo(200)
                .jsonPath("$[0].product.branchId").isEqualTo(11);
    }

    @Test
    void getMaxStockProducts_businessError_400() {
        when(getMaxstockProductsUseCase.execute(999L))
                .thenReturn(Flux.error(new BusinessRuleException("Error","Franquicia no encontrada")));

        client.get()
                .uri("/api/franchises/{id}/max-stock-products", 999)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.Error").isEqualTo("Franquicia no encontrada");
    }

    @Test
    void shouldThrowBusinessRuleWhenIdIsNotNumeric() {
        var httpRequest = MockServerHttpRequest.delete("/api/products/xyz").build();
        var exchange = MockServerWebExchange.from(httpRequest);

        ServerRequest request = ServerRequest.create(
                exchange, withDefaults().messageReaders()
        );

        var ex = assertThrows(IllegalArgumentException.class,
                () -> handler.deleteProduct(request)
        );
        assertThat(ex.getMessage()).isEqualTo("No path variable with name \"id\" available");
    }
}