package co.com.bancolombia.api.dto.response;

public record ProductResponse(
        String name,
        Integer stock,
        Long branchId) {
}
