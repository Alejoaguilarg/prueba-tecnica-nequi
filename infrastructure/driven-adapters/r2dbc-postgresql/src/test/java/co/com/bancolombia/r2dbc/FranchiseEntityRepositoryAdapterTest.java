package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.adapters.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.FranchiseEntity;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.r2dbc.repositories.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(franchiseRepository, mapper);
    }

    @Test
    void saveFranchise_shouldSaveSuccessfully() {
        // Given
        Franchise domain = Franchise.builder()
                .franchiseId(1L)
                .name("Acme")
                .build();

        FranchiseEntity entity = FranchiseEntity.builder()
                .franchiseId(1L)
                .name("Acme")
                .build();

        when(mapper.map(any(), eq(FranchiseEntity.class))).thenReturn(entity);
        when(franchiseRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(any(), eq(Franchise.class))).thenReturn(domain);

        // When / Then
        StepVerifier.create(adapter.saveFranchise(domain))
                .expectNext(domain)
                .verifyComplete();

        verify(franchiseRepository).save(entity);
        verify(mapper).map(domain, FranchiseEntity.class);
        verify(mapper).map(entity, Franchise.class);
        verifyNoMoreInteractions(franchiseRepository, mapper);
    }

    @Test
    void saveFranchise_shouldFailWhenRepositoryErrors() {
        // Given
        Franchise domain = Franchise.builder()
                .franchiseId(2L)
                .name("Beta")
                .build();

        FranchiseEntity entity = FranchiseEntity.builder()
                .franchiseId(2L)
                .name("Beta")
                .build();

        when(mapper.map(domain, FranchiseEntity.class)).thenReturn(entity);
        when(franchiseRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));

        // When / Then
        StepVerifier.create(adapter.saveFranchise(domain))
                .expectErrorMatches(ex -> ex instanceof RuntimeException && ex.getMessage().equals("DB error"))
                .verify();

        verify(franchiseRepository, times(1)).save(entity);
    }
}
