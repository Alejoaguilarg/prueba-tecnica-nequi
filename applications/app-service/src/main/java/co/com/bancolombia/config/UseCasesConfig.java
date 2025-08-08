package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateways.IFranchiseRepository;
import co.com.bancolombia.model.product.gateways.IProductRepository;
import co.com.bancolombia.r2dbc.adapters.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.adapters.ProductRepositoryAdapter;
import co.com.bancolombia.usecase.branch.AddBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.product.*;
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

        @Bean
        public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepositoryAdapter franchiseRepositoryAdapter) {
            return new UpdateFranchiseNameUseCase(franchiseRepositoryAdapter);
        }

        @Bean
        public GetMaxstockProductsUseCase getMaxstockProductsUseCase(IProductRepository productRepository, IFranchiseRepository franchiseRepository) {
            return new GetMaxstockProductsUseCase(productRepository, franchiseRepository);
        }
}
