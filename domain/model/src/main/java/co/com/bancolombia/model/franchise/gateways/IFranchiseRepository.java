package co.com.bancolombia.model.franchise.gateways;

import co.com.bancolombia.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchiseRepository {
    Mono<Franchise> saveFranchise(Franchise franchise);
    Mono<Franchise> findById(Long id);
}
