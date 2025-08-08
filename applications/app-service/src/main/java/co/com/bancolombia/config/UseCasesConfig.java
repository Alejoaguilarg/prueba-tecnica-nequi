package co.com.bancolombia.config;

import co.com.bancolombia.r2dbc.adapters.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.usecase.AddBranchUseCase;
import co.com.bancolombia.usecase.AddProductUseCase;
import co.com.bancolombia.usecase.CreateFranchiseUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

        @Bean
        public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepositoryAdapter franchiseRepositoryAdapter) {
                return new CreateFranchiseUseCase(franchiseRepositoryAdapter);
        }

        @Bean
        public AddBranchUseCase addBranchUseCase(BranchRepositoryAdapter branchRepositoryAdapter) {
                return new AddBranchUseCase(branchRepositoryAdapter);
        }

        @Bean
        public AddProductUseCase addProductUseCase(ProductRepositoryAdapter productRepositoryAdapter) {
                return new AddProductUseCase(productRepositoryAdapter);
        }
}
