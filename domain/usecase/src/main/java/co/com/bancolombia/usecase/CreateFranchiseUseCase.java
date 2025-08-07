package co.com.bancolombia.usecase;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import reactor.core.publisher.Mono;

public class CreateFranchiseUseCase {
    
    private final IFranchiseRepository repository;

    public CreateFranchiseUseCase(IFranchiseRepository iFranchiseRepository) {
        repository = iFranchiseRepository;
    }
    
    public Mono<Franchise> execute(Franchise franchise) {
        return repository.saveFranchise(franchise);
    }
}
