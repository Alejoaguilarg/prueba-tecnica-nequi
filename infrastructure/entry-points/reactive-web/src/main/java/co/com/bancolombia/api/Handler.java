package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.UpdateBrachNameRequest;
import co.com.bancolombia.api.dto.request.UpdateProductNameRequest;
import co.com.bancolombia.api.dto.request.UpdateStockRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.dto.response.FranchiseResponse;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.ex.BusinessRuleException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.branch.AddBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.DeleteProductUseCase;
import co.com.bancolombia.usecase.product.AddProductUseCase;
import co.com.bancolombia.usecase.product.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.product.UpdateProductStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request
                .bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::execute)
                .flatMap(saved -> ServerResponse
                        .created(URI.create("/api/franchises/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new FranchiseResponse(saved.getName()))
                );
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        return request
                .bodyToMono(Branch.class)
                .flatMap(addBranchUseCase::execute)
                .flatMap(added -> ServerResponse
                        .created(URI.create("/api/branches/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new BranchResponse(added.getName(), added.getFranchiseId()))
                );
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        return request
                .bodyToMono(Product.class)
                .flatMap(addProductUseCase::execute)
                .flatMap(added -> ServerResponse
                        .created(URI.create("/api/products/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(added.getName(), added.getStock(), added.getBranchId()))
                );
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        final Long id = parseId(request.pathVariable("id"));
        return deleteProductUseCase.execute(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        final Long id = parseId(request.pathVariable("id"));
        return request
                .bodyToMono(UpdateStockRequest.class)
                .flatMap(updateStockRequest -> updateProductStockUseCase
                        .execute(id, updateStockRequest.stock()))
                .flatMap(updated -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(updated.getName(), updated.getStock(), updated.getBranchId())));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        final Long id = parseId(request.pathVariable("id"));
        return request
                .bodyToMono(UpdateProductNameRequest.class)
                .flatMap(updateProductNameRequest -> updateProductNameUseCase
                        .execute(id,updateProductNameRequest.name())
                .flatMap(updated -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ProductResponse(updated.getName(), updated.getStock(), updated.getBranchId())))
                );
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        final Long id = parseId(request.pathVariable("id"));

        return request
                .bodyToMono(UpdateBrachNameRequest.class)
                .flatMap(updateBranchNameRequest -> updateBranchNameUseCase
                        .execute(id, updateBranchNameRequest.name()))
                .flatMap(updated -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new BranchResponse(updated.getName(), updated.getFranchiseId())));
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BusinessRuleException("Error", "El ID debe ser un número válido.");
        }
    }
}
