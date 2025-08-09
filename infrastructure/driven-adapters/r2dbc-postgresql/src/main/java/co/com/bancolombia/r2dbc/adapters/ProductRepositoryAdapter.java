package co.com.bancolombia.r2dbc.adapters;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import co.com.bancolombia.model.valueobjects.BranchTopProduct;
import co.com.bancolombia.r2dbc.entities.ProductEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.ProductRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<
        Product,
        ProductEntity,
        Long,
        ProductRepository
        > implements IProductRepository {

    private final DatabaseClient client;

    public ProductRepositoryAdapter(ProductRepository repository, ObjectMapper mapper, DatabaseClient client) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
        this.client = client;
    }

    @Override
    public Mono<Product> saveProduct(Product product) {
        return this.save(product);
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<BranchTopProduct> findTopProductByFranchise(Long franchiseId) {
        String sql = """
        SELECT b.branch_id       AS branch_id,
               b.name            AS branch_name,
               p.id              AS product_id,
               p.name            AS product_name,
               p.stock           AS stock
        FROM branch b
        JOIN LATERAL (
            SELECT id, name, stock
            FROM products
            WHERE branch_id = b.branch_id
            ORDER BY stock DESC, id ASC
            LIMIT 1
        ) p ON TRUE
        WHERE b.franchise_id = :franchiseId
        """;

        return client.sql(sql)
                .bind("franchiseId", franchiseId)
                .map((row, metadata) -> BranchTopProduct.builder()
                        .branchId(row.get("branch_id", Long.class))
                        .branchName(row.get("branch_name", String.class))
                        .product(Product.builder()
                                .id(row.get("product_id", Long.class))
                                .name(row.get("product_name", String.class))
                                .stock(row.get("stock", Integer.class))
                                .branchId(row.get("branch_id", Long.class))
                                .build())
                        .build())
                .all();
    }
}
