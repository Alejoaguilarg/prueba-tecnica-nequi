package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import co.com.bancolombia.model.valueobjects.BranchTopProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetMaxstockProductsUseCase {
    private final IProductRepository productRepository;
    private final IFranchiseRepository franchiseRepository;

    public GetMaxstockProductsUseCase(IProductRepository productRepository, IFranchiseRepository franchiseRepository) {
        this.productRepository = productRepository;
        this.franchiseRepository = franchiseRepository;
    }

    public Flux<BranchTopProduct> execute(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessRuleException("Error", "Franquicia no encontrada")))
                .flatMapMany(franchise -> productRepository.findTopProductByFranchise(franchiseId));
    }


}
