package co.com.bancolombia.usecase;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddProductUseCaseTest {

    @Mock
    private IProductRepository repository;

    private AddProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new AddProductUseCase(repository);
    }

    @Test
    void executeShouldDelegateToRepositoryAndReturnSavedEntity() {
        Product input = Product.builder().name("Pencil").stock(10).branchId(5L).build();
        Product saved = Product.builder().name("Pencil").stock(10).branchId(5L).build();

        when(repository.saveProduct(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(input))
                .expectNext(saved)
                .verifyComplete();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveProduct(captor.capture());
        assertEquals("Pencil", captor.getValue().getName());
        assertEquals(10, captor.getValue().getStock());
        assertEquals(5L, captor.getValue().getBranchId());
    }

    @Test
    void executeShouldPropagateErrors() {
        Product input = Product.builder().name("Err").stock(0).branchId(9L).build();
        when(repository.saveProduct(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("db error product")));

        StepVerifier.create(useCase.execute(input))
                .expectErrorMessage("db error product")
                .verify();
    }
}
