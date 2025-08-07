package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.r2dbc.entities.FranchiseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseEntityRepositoryAdapterTest {

    @InjectMocks
    FranchiseRepositoryAdapter repositoryAdapter;

    @Mock
    FranchiseRepository repository;

    @Mock
    ObjectMapper mapper;

    private Franchise domain;
    private FranchiseEntity entity;

    @BeforeEach
    void init() {
        domain = Franchise.builder().franchiseId(1L).name("ACME").build();
        entity = FranchiseEntity.builder().franchiseId(1L).name("ACME").build();

        // mapper from data to domain used in all tests
        when(mapper.map(entity, Franchise.class)).thenReturn(domain);
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        StepVerifier.create(repositoryAdapter.findById(1L))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(entity));
        StepVerifier.create(repositoryAdapter.findAll())
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(mapper.map(domain, FranchiseEntity.class)).thenReturn(entity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        StepVerifier.create(repositoryAdapter.findByExample(domain))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(domain, FranchiseEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        StepVerifier.create(repositoryAdapter.saveFranchise(domain))
                .expectNext(domain)
                .verifyComplete();
    }
}
