package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.IBranchRepository;
import reactor.core.publisher.Mono;

public class AddBranchUseCase {

    private final IBranchRepository repository;

    public AddBranchUseCase(IBranchRepository repository) {
        this.repository = repository;
    }

    public Mono<Branch> execute(Branch branch) {
        return repository.saveBranch(branch);
    }
}
