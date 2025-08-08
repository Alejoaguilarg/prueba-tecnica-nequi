package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long>,
        ReactiveQueryByExampleExecutor<ProductEntity> {
}
