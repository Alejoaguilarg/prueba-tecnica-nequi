package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Mono;

public interface IProductRepository {
    Mono<Product> saveProduct(Product product);
}
