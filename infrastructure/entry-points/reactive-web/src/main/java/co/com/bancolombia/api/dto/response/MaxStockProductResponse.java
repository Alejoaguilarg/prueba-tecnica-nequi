package co.com.bancolombia.api.dto.response;

public record MaxStockProductResponse(
        String branchName,
        ProductResponse product
) {}
