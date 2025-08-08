package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.IBranchRepository;
import co.com.bancolombia.model.ex.BusinessRuleException;
import reactor.core.publisher.Mono;

public class UpdateBranchNameUseCase {

    private final IBranchRepository repository;

    public UpdateBranchNameUseCase(IBranchRepository iBranchRepository) {
        repository = iBranchRepository;
    }

    public Mono<Branch> execute(Long id, String name) {
        if (name.length() < 2 || name.length() > 20) {
            return Mono.error(new BusinessRuleException("Error", "Nombre de sucursal invalido"));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessRuleException("Error", "Sucursal no encontrada.")))
                .flatMap(branch -> {
                    branch.setName(name);
                    return repository.saveBranch(branch);
                });
    }
}
