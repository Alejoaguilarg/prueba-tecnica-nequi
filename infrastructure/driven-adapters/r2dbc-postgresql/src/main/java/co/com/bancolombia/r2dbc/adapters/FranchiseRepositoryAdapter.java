package co.com.bancolombia.r2dbc.adapters;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import co.com.bancolombia.r2dbc.entities.FranchiseEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.FranchiseRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseRepositoryAdapter extends ReactiveAdapterOperations<
        Franchise,
        FranchiseEntity,
        Long,
        FranchiseRepository
> implements IFranchiseRepository {
    public FranchiseRepositoryAdapter(FranchiseRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return this.save(franchise);
    }
}
