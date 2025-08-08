package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.r2dbc.adapters.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.BranchEntity;
import co.com.bancolombia.r2dbc.repositories.BranchRepository;
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
class BranchRepositoryAdapterTest {

    @InjectMocks
    BranchRepositoryAdapter repositoryAdapter;

    @Mock
    BranchRepository repository;

    @Mock
    ObjectMapper mapper;

    private Branch domain;
    private BranchEntity entity;

    @BeforeEach
    void init() {
        domain = Branch.builder().branchId(1L).name("B1").franchiseId(2L).build();
        entity = BranchEntity.builder().branchId(1L).name("B1").franchiseId(2L).build();

        when(mapper.map(entity, Branch.class)).thenReturn(domain);
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
        when(mapper.map(domain, BranchEntity.class)).thenReturn(entity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        StepVerifier.create(repositoryAdapter.findByExample(domain))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(domain, BranchEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        StepVerifier.create(repositoryAdapter.saveBranch(domain))
                .expectNext(domain)
                .verifyComplete();
    }
}
