package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import reactor.core.publisher.Mono;

public class UpdateProductStockUseCase {

    private final IProductRepository repository;

    public UpdateProductStockUseCase(IProductRepository repository) {
        this.repository = repository;
    }

    public Mono<Product> execute(Long id, int stock) {
        if (stock < 0) {
            return Mono.error(new BusinessRuleException("Error","Stock debe ser mayor o igual a 0"));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessRuleException("Error", "Producto no encontrado.")))
                .flatMap(product -> {
                    product.setStock(stock);
                    return repository.saveProduct(product);
                });
    }
}
