package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.product.gateways.IProductRepository;
import reactor.core.publisher.Mono;

public class DeleteProductUseCase {

    private final IProductRepository repository;

    public DeleteProductUseCase(IProductRepository repository) {
        this.repository = repository;
    }

    public Mono<Void> execute(Long id) {
        return repository.deleteProduct(id);
    }
}
