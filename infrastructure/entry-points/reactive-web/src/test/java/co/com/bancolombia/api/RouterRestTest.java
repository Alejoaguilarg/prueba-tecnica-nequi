package co.com.bancolombia.api;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.AddBranchUseCase;
import co.com.bancolombia.usecase.AddProductUseCase;
import co.com.bancolombia.usecase.CreateFranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateFranchiseUseCase createFranchiseUseCase;
    @MockBean
    private AddBranchUseCase addBranchUseCase;
    @MockBean
    private AddProductUseCase addProductUseCase;

    @Test
    void createFranchise_success() {
        Franchise input = Franchise.builder().name("ACME").build();
        Franchise saved = Franchise.builder().franchiseId(1L).name("ACME").build();
        when(createFranchiseUseCase.execute(any(Franchise.class))).thenReturn(Mono.just(saved));

        webTestClient.post().uri("/api/franchises")
                .bodyValue(input)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/franchises/")
                .expectBody()
                .jsonPath("$.name").isEqualTo("ACME")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }

    @Test
    void createFranchise_error() {
        Franchise input = Franchise.builder().name("ERR").build();
        when(createFranchiseUseCase.execute(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        webTestClient.post().uri("/api/franchises")
                .bodyValue(input)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No se pudo crear la franquicia")
                .jsonPath("$.details").isEqualTo("boom");
    }

    @Test
    void addBranch_success() {
        Branch input = Branch.builder().name("B1").franchiseId(9L).build();
        Branch saved = Branch.builder().branchId(2L).name("B1").franchiseId(9L).build();
        when(addBranchUseCase.execute(any(Branch.class))).thenReturn(Mono.just(saved));

        webTestClient.post().uri("/api/branches")
                .bodyValue(input)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/branches/")
                .expectBody()
                .jsonPath("$.name").isEqualTo("B1")
                .jsonPath("$.branchId").isEqualTo(2);
    }

    @Test
    void addBranch_error() {
        Branch input = Branch.builder().name("Bbad").franchiseId(1L).build();
        when(addBranchUseCase.execute(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("db err")));

        webTestClient.post().uri("/api/branches")
                .bodyValue(input)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No se pudo agregar la sucursal")
                .jsonPath("$.details").isEqualTo("db err");
    }

    @Test
    void addProduct_success() {
        Product input = Product.builder().name("Pencil").stock(10).branchId(1L).build();
        Product saved = Product.builder().name("Pencil").stock(10).branchId(1L).build();
        when(addProductUseCase.execute(any(Product.class))).thenReturn(Mono.just(saved));

        webTestClient.post().uri("/api/products")
                .bodyValue(input)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/products/")
                .expectBody()
                .jsonPath("$.name").isEqualTo("Pencil")
                .jsonPath("$.stock").isEqualTo(10)
                .jsonPath("$.branchId").isEqualTo(1);
    }

    @Test
    void addProduct_error() {
        Product input = Product.builder().name("Bad").stock(0).branchId(1L).build();
        when(addProductUseCase.execute(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("prod err")));

        webTestClient.post().uri("/api/products")
                .bodyValue(input)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No se pudo agregar el producto")
                .jsonPath("$.details").isEqualTo("prod err");
    }
}
