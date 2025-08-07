package co.com.bancolombia.usecase;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateFranchiseUseCaseTest {

    @Mock
    private IFranchiseRepository repository;

    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CreateFranchiseUseCase(repository);
    }

    @Test
    void executeShouldDelegateToRepositoryAndReturnSavedEntity() {
        Franchise input = Franchise.builder().name("ACME").build();
        Franchise saved = Franchise.builder().franchiseId(1L).name("ACME").build();

        when(repository.saveFranchise(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(input))
                .expectNext(saved)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(repository).saveFranchise(captor.capture());
        assertEquals("ACME", captor.getValue().getName());
    }

    @Test
    void executeShouldPropagateErrors() {
        Franchise input = Franchise.builder().name("ERR").build();
        when(repository.saveFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(useCase.execute(input))
                .expectErrorMessage("db error")
                .verify();
    }
}
