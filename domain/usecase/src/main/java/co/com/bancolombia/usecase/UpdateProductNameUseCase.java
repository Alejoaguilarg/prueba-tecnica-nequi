package co.com.bancolombia.usecase;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import reactor.core.publisher.Mono;

public class UpdateProductNameUseCase {

    private final IProductRepository repository;

    public UpdateProductNameUseCase(IProductRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(Long id, String name) {
        if (name.length() < 2 || name.length() > 20) {
            return Mono.error(new BusinessRuleException("Error", "Nombre de producto invalido"));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessRuleException("Error", "Producto no encontrado.")))
                .flatMap(product -> {
                    product.setName(name);
                    return repository.saveProduct(product);
                });
    }
}
