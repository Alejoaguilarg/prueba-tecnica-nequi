package co.com.bancolombia.config;

import co.com.bancolombia.r2dbc.adapters.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.usecase.branch.AddBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.DeleteProductUseCase;
import co.com.bancolombia.usecase.product.AddProductUseCase;
import co.com.bancolombia.usecase.product.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.product.UpdateProductStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

        @Bean
        public DeleteProductUseCase deleteProductUseCase(ProductRepositoryAdapter productRepositoryAdapter) {
                return new  DeleteProductUseCase(productRepositoryAdapter);
        }

        @Bean
        public UpdateProductStockUseCase updateProductStockUseCase(ProductRepositoryAdapter productRepositoryAdapter) {
            return new UpdateProductStockUseCase(productRepositoryAdapter);
        }

        @Bean
        public UpdateProductNameUseCase updateProductNameUseCase(ProductRepositoryAdapter productRepositoryAdapter) {
            return new UpdateProductNameUseCase(productRepositoryAdapter);
        }

        @Bean
        public UpdateBranchNameUseCase updateBranchNameUseCase(BranchRepositoryAdapter branchRepositoryAdapter) {
            return new UpdateBranchNameUseCase(branchRepositoryAdapter);
        }
}
