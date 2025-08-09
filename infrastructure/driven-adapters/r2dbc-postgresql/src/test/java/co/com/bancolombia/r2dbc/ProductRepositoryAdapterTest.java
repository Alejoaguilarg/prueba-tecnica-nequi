package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.valueobjects.BranchTopProduct;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.ProductEntity;
import co.com.bancolombia.r2dbc.repositories.ProductRepository;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DatabaseClient client;

    @Mock
    DatabaseClient.GenericExecuteSpec sqlSpec;

    @Mock
    RowsFetchSpec<BranchTopProduct> rowsSpec;

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

    @Test
    void findTopProductByFranchisePropagatesError() {
        when(client.sql(anyString())).thenReturn(sqlSpec);
        when(sqlSpec.bind(eq("franchiseId"), anyLong())).thenReturn(sqlSpec);

        when(sqlSpec.map(ArgumentMatchers.<BiFunction<Row, RowMetadata, BranchTopProduct>>any()))
                .thenReturn(rowsSpec);

        when(rowsSpec.all()).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findTopProductByFranchise(1L))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void findTopProductByFranchiseEmptyResult() {
        when(client.sql(anyString())).thenReturn(sqlSpec);
        when(sqlSpec.bind(eq("franchiseId"), anyLong())).thenReturn(sqlSpec);
        when(sqlSpec.map(Mockito.<BiFunction<Row, RowMetadata, BranchTopProduct>>any()))
                .thenReturn(rowsSpec);
        when(rowsSpec.all()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findTopProductByFranchise(999L))
                .verifyComplete();

        verify(rowsSpec).all();
    }

    @Test
    void findTopProductByFranchise_happyPath() {
        Long fid = 1L;

        when(client.sql(Mockito.<String>any())).thenReturn(sqlSpec);

        when(sqlSpec.bind(("franchiseId"), (fid))).thenReturn(sqlSpec);

        when(sqlSpec.map(Mockito.<BiFunction<Row, RowMetadata, BranchTopProduct>>any()))
                .thenReturn(rowsSpec);

        BranchTopProduct expected = BranchTopProduct.builder()
                .branchId(11L).branchName("Sucursal Centro")
                .product(Product.builder().id(101L).name("Coca-Cola").stock(200).branchId(11L).build())
                .build();

        when(rowsSpec.all()).thenReturn(Flux.just(expected));

        StepVerifier.create(adapter.findTopProductByFranchise(fid))
                .expectNext(expected)
                .verifyComplete();

        verify(client).sql(ArgumentMatchers.<String>argThat(sql ->
                sql.contains("JOIN LATERAL") && sql.contains("WHERE b.franchise_id = :franchiseId")
        ));
        verify(sqlSpec).bind("franchiseId", fid);
        verify(sqlSpec).map(Mockito.<BiFunction<Row, RowMetadata, BranchTopProduct>>any());
        verify(rowsSpec).all();
    }

    @Test
    void findTopProductByFranchise_mapsRowCorrectly() {
        Long fid = 1L;

        when(client.sql(Mockito.<String>any())).thenReturn(sqlSpec);
        when(sqlSpec.bind(("franchiseId"), (fid))).thenReturn(sqlSpec);

        ArgumentCaptor<BiFunction<Row, RowMetadata, BranchTopProduct>> captor =
                ArgumentCaptor.forClass(BiFunction.class);
        when(sqlSpec.map(captor.capture())).thenReturn(rowsSpec);

        Row row = mock(Row.class);
        RowMetadata meta = mock(RowMetadata.class);
        when(row.get("branch_id", Long.class)).thenReturn(11L);
        when(row.get("branch_name", String.class)).thenReturn("Sucursal Centro");
        when(row.get("product_id", Long.class)).thenReturn(101L);
        when(row.get("product_name", String.class)).thenReturn("Coca-Cola");
        when(row.get("stock", Integer.class)).thenReturn(200);

        when(rowsSpec.all()).thenAnswer(inv ->
                Flux.just(captor.getValue().apply(row, meta)));

        StepVerifier.create(adapter.findTopProductByFranchise(fid))
                .assertNext(btp -> {
                    assertThat(btp.branchId()).isEqualTo(11L);
                    assertThat(btp.branchName()).isEqualTo("Sucursal Centro");
                    assertThat(btp.product().getId()).isEqualTo(101L);
                    assertThat(btp.product().getName()).isEqualTo("Coca-Cola");
                    assertThat(btp.product().getStock()).isEqualTo(200);
                    assertThat(btp.product().getBranchId()).isEqualTo(11L);
                })
                .verifyComplete();

        verify(client).sql(Mockito.<String>any());
        verify(sqlSpec).bind("franchiseId", fid);
        verify(rowsSpec).all();

        verify(row, atLeastOnce()).get("branch_id", Long.class);
        verify(row).get("branch_name", String.class);
        verify(row).get("product_id", Long.class);
        verify(row).get("product_name", String.class);
        verify(row).get("stock", Integer.class);
    }

}
