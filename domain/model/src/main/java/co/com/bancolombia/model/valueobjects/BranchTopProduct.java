package co.com.bancolombia.model.valueobjects;

import co.com.bancolombia.model.product.Product;
import lombok.Builder;

@Builder
public record BranchTopProduct(
        Long branchId,
        String branchName,
        Product product) {}

