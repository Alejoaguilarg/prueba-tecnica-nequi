package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.adapters.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.repositories.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class FranchiseEntityRepositoryAdapterTest {

    @InjectMocks
    FranchiseRepositoryAdapter repositoryAdapter;

    @Mock
    FranchiseRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {
    }

    @Test
    void mustFindAllValues() {
    }

    @Test
    void mustFindByExample() {
    }

    @Test
    void mustSaveValue() {
    }
}
