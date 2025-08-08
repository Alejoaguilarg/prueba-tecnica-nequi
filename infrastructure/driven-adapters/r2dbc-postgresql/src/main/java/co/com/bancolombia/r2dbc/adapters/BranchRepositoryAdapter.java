package co.com.bancolombia.r2dbc.adapters;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.IBranchRepository;
import co.com.bancolombia.r2dbc.entities.BranchEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.BranchRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BranchRepositoryAdapter extends ReactiveAdapterOperations<
        Branch,
        BranchEntity,
        Long,
        BranchRepository> implements IBranchRepository {
    public BranchRepositoryAdapter(BranchRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    @Override
    public Mono<Branch> saveBranch(Branch branch) {
        return this.save(branch);
    }
}
