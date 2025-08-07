package co.com.bancolombia.api;

import co.com.bancolombia.model.franchise.Franchise;
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

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }
}
