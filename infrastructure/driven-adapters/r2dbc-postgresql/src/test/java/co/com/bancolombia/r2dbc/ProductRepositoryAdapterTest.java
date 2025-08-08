package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.ProductEntity;
import co.com.bancolombia.r2dbc.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DatabaseClient client;

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(productRepository, mapper, client);
    }

    @Test
    void shouldSaveSuccessfully() {
        // Given
        Product domain = Product.builder()
                .id(1L)
                .name("Pepsi")
                .stock(100)
                .branchId(10L)
                .build();

        ProductEntity entity = new ProductEntity();
        entity.setProductId(1L);
        entity.setName("Pepsi");
        entity.setStock(100);
        entity.setBranchId(10L);

        // Mocks
        when(mapper.map(domain, ProductEntity.class)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Product.class)).thenReturn(domain);

        // When & Then
        StepVerifier.create(adapter.saveProduct(domain))
                .expectNext(domain)
                .verifyComplete();

        verify(productRepository).save(entity);
    }

    @Test
    void shouldCallDeleteById() {
        // Given
        Long id = 99L;

        // Mock
        when(productRepository.deleteById(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.deleteProduct(id))
                .verifyComplete();

        verify(productRepository).deleteById(id);
    }

    @Test
    void shouldFailOnRepositoryError() {
        Product domain = Product.builder().id(1L).name("X").stock(1).branchId(1L).build();
        ProductEntity entity = new ProductEntity();
        entity.setProductId(1L);

        when(mapper.map(domain, ProductEntity.class)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.saveProduct(domain))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();
    }

}
