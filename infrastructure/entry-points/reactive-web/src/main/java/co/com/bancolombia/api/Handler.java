package co.com.bancolombia.api;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.AddBranchUseCase;
import co.com.bancolombia.usecase.CreateFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request
                .bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::execute)
                .flatMap(saved -> ServerResponse
                        .created(URI.create("/api/franchises/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved)
                )
                .onErrorResume(e -> ServerResponse
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "error", "No se pudo crear la franquicia",
                                "details", e.getMessage()
                        ))
                );
    }
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        return request
                .bodyToMono(Branch.class)
                .flatMap(addBranchUseCase::execute)
                .flatMap(added -> ServerResponse
                        .created(URI.create("/api/branches/"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(added)
                        .onErrorResume(e -> ServerResponse
                                .badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of(
                                        "error", "No se pudo agregar la sucursal",
                                        "details", e.getMessage()
                                ))
                        ));
    }
}
