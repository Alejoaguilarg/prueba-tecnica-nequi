package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.valueobjects.BranchTopProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductRepository {
    Mono<Product> saveProduct(Product product);
    Mono<Void> deleteProduct(Long id);
    Mono<Product> findById(Long id);
    Flux<BranchTopProduct> findTopProductByFranchise(Long franchiseId);
}
