package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.ProductEntity;
import co.com.bancolombia.r2dbc.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @InjectMocks
    ProductRepositoryAdapter repositoryAdapter;

    @Mock
    ProductRepository repository;

    @Mock
    ObjectMapper mapper;

    private Product domain;
    private ProductEntity entity;

    @BeforeEach
    void init() {
        domain = Product.builder().name("Pencil").stock(10).branchId(5L).build();
        entity = ProductEntity.builder().id(1L).name("Pencil").stock(10).branchId(5L).build();

        when(mapper.map(entity, Product.class)).thenReturn(domain);
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        StepVerifier.create(repositoryAdapter.findById(1L))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(entity));
        StepVerifier.create(repositoryAdapter.findAll())
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(mapper.map(domain, ProductEntity.class)).thenReturn(entity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        StepVerifier.create(repositoryAdapter.findByExample(domain))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(domain, ProductEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        StepVerifier.create(repositoryAdapter.saveProduct(domain))
                .expectNext(domain)
                .verifyComplete();
    }
}
