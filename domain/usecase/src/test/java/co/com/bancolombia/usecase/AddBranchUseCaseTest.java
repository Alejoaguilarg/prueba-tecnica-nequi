package co.com.bancolombia.usecase;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.IBranchRepository;
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

class AddBranchUseCaseTest {

    @Mock
    private IBranchRepository repository;

    private AddBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new AddBranchUseCase(repository);
    }

    @Test
    void executeShouldDelegateToRepositoryAndReturnSavedEntity() {
        Branch input = Branch.builder().name("Branch A").franchiseId(5L).build();
        Branch saved = Branch.builder().branchId(1L).name("Branch A").franchiseId(5L).build();

        when(repository.saveBranch(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(input))
                .expectNext(saved)
                .verifyComplete();

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(repository).saveBranch(captor.capture());
        assertEquals("Branch A", captor.getValue().getName());
        assertEquals(5L, captor.getValue().getFranchiseId());
    }

    @Test
    void executeShouldPropagateErrors() {
        Branch input = Branch.builder().name("Err").franchiseId(9L).build();
        when(repository.saveBranch(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("db error branch")));

        StepVerifier.create(useCase.execute(input))
                .expectErrorMessage("db error branch")
                .verify();
    }
}
