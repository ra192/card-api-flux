package com.card.handler;

import com.card.service.CardService;
import com.card.service.dto.CreateCardDto;
import com.card.service.dto.CreateCardTransactionDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CardHandler {
    private final CardService cardService;

    public CardHandler(CardService cardService) {
        this.cardService = cardService;
    }

    public Mono<ServerResponse>createVirtual(ServerRequest request) {
        return request.bodyToMono(CreateCardDto.class).flatMap(cardService::createVirtual)
                .flatMap(res->ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res));
    }

    public Mono<ServerResponse>deposit(ServerRequest request) {
        return request.bodyToMono(CreateCardTransactionDto.class).flatMap(cardService::deposit)
                .flatMap(res->ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                .onErrorResume(e->ok().contentType(MediaType.APPLICATION_JSON).bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse>withdraw(ServerRequest request) {
        return request.bodyToMono(CreateCardTransactionDto.class).flatMap(cardService::withdraw)
                .flatMap(res->ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res));
    }
}
