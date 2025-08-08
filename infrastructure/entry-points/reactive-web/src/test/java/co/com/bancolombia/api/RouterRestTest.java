package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private Handler handler;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        RouterFunction<ServerResponse> routerFunction = new RouterRest()
                .routerFunction(handler);

        client = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldRouteToCreateFranchise() {
        when(handler.createFranchise(any())).thenReturn(ServerResponse.ok().build());

        client.post()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isOk();

        verify(handler).createFranchise(any());
    }

    @Test
    void shouldRouteToAddBranch() {
        when(handler.addBranch(any())).thenReturn(ServerResponse.ok().build());

        client.post()
                .uri("/api/branches")
                .exchange()
                .expectStatus().isOk();

        verify(handler).addBranch(any());
    }

    @Test
    void shouldRouteToAddProduct() {
        when(handler.addProduct(any())).thenReturn(ServerResponse.ok().build());

        client.post()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk();

        verify(handler).addProduct(any());
    }

    @Test
    void shouldRouteToDeleteProduct() {
        when(handler.deleteProduct(any())).thenReturn(ServerResponse.ok().build());

        client.delete()
                .uri("/api/products/10")
                .exchange()
                .expectStatus().isOk();

        verify(handler).deleteProduct(any());
    }

    @Test
    void shouldRouteToUpdateProductStock() {
        when(handler.updateProductStock(any())).thenReturn(ServerResponse.ok().build());

        client.patch()
                .uri("/api/products/10/stock")
                .body(BodyInserters.fromValue(50))
                .exchange()
                .expectStatus().isOk();

        verify(handler).updateProductStock(any());
    }

    @Test
    void shouldRouteToUpdateProductName() {
        when(handler.updateProductName(any())).thenReturn(ServerResponse.ok().build());

        client.patch()
                .uri("/api/products/10/name")
                .body(BodyInserters.fromValue("Pepsi"))
                .exchange()
                .expectStatus().isOk();

        verify(handler).updateProductName(any());
    }

    @Test
    void shouldRouteToUpdateBranchName() {
        when(handler.updateBranchName(any())).thenReturn(ServerResponse.ok().build());

        client.patch()
                .uri("/api/branches/10/name")
                .body(BodyInserters.fromValue("another"))
                .exchange()
                .expectStatus().isOk();

        verify(handler).updateBranchName(any());
    }

    @Test
    void shouldRouteToUpdateFranchiseName() {
        when(handler.updateFranchiseName(any())).thenReturn(ServerResponse.ok().build());

        client.patch()
                .uri("/api/franchises/10/name")
                .body(BodyInserters.fromValue("another"))
                .exchange()
                .expectStatus().isOk();

        verify(handler).updateFranchiseName(any());
    }

    @Test
    void shouldRouteToGetMaxStockProducts() {
        when(handler.getMaxStockProductsByFranchiseId(any())).thenReturn(ServerResponse.ok().build());

        client.get()
                .uri("/api/franchises/10/max-stock-products")
                .exchange()
                .expectStatus().isOk();

        verify(handler).getMaxStockProductsByFranchiseId(any());
    }
}
