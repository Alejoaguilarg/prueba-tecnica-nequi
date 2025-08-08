package co.com.bancolombia.r2dbc.adapters;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import co.com.bancolombia.r2dbc.entities.ProductEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.ProductRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<
        Product,
        ProductEntity,
        Long,
        ProductRepository
        > implements IProductRepository {
    public ProductRepositoryAdapter(ProductRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
    }

    @Override
    public Mono<Product> saveProduct(Product product) {
        return this.save(product);
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        return repository.deleteById(id);
    }
}
