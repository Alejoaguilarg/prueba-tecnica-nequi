package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import reactor.core.publisher.Mono;

public class UpdateFranchiseNameUseCase {

    private final IFranchiseRepository repository;

    public UpdateFranchiseNameUseCase(IFranchiseRepository iFranchiseRepository) {
        repository = iFranchiseRepository;
    }

    public Mono<Franchise> execute(Long id, String name) {
        if (name.length() < 2 || name.length() > 20) {
            return Mono.error(new BusinessRuleException("Error", "Nombre de franquicia incorrecto"));
        }
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessRuleException("Error", "Franquicia no encontrada")))
                .flatMap(franchise -> {
                    franchise.setName(name);
                    return repository.saveFranchise(franchise);
                });
    }
}
