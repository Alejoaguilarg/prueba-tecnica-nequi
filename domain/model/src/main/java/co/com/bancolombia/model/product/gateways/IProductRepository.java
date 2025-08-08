package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Mono;

public interface IProductRepository {
    Mono<Product> saveProduct(Product product);
    Mono<Void> deleteProduct(Long id);
    Mono<Product> findById(Long id);
}
