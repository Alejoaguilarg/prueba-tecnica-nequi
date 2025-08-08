package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface IBranchRepository {
    Mono<Branch> saveBranch(Branch branch);
    Mono<Branch> findById(Long id);

}
