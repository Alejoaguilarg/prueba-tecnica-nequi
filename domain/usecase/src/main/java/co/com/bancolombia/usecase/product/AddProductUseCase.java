package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import reactor.core.publisher.Mono;

public class AddProductUseCase {
    private final IProductRepository repository;

    public AddProductUseCase(IProductRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(Product product) {
        return repository.saveProduct(product);
    }
}
