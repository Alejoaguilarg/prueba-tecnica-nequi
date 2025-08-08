package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.r2dbc.adapters.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.BranchEntity;
import co.com.bancolombia.r2dbc.repositories.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchRepositoryAdapterTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ObjectMapper mapper;

    private BranchRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(branchRepository, mapper);
    }

    @Test
    void shouldSaveSuccessfully() {
        // Given
        Branch domain = Branch.builder()
                .id(1L)
                .name("Sucursal Medellín")
                .franchiseId(10L)
                .build();

        BranchEntity entity = new BranchEntity();
        entity.setBranchId(1L);
        entity.setName("Sucursal Medellín");
        entity.setFranchiseId(10L);

        // Mocks
        when(mapper.map(domain, BranchEntity.class)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Branch.class)).thenReturn(domain);

        // When & Then
        StepVerifier.create(adapter.saveBranch(domain))
                .expectNext(domain)
                .verifyComplete();

        verify(branchRepository).save(entity);
    }

    @Test
    void shouldReturnErrorIfRepositoryFails() {
        // Given
        Branch domain = Branch.builder()
                .id(2L)
                .name("Sucursal Bogotá")
                .franchiseId(20L)
                .build();

        BranchEntity entity = new BranchEntity();
        entity.setBranchId(2L);
        entity.setName("Sucursal Bogotá");
        entity.setFranchiseId(20L);

        // Mocks
        when(mapper.map(domain, BranchEntity.class)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));

        // When & Then
        StepVerifier.create(adapter.saveBranch(domain))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();

        verify(branchRepository).save(entity);
    }
}

