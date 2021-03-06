package com.card.handler;

import com.card.dto.CreateCardDto;
import com.card.dto.CreateCardTransactionDto;
import com.card.dto.ErrorDto;
import com.card.entity.Card;
import com.card.entity.enums.CardType;
import com.card.service.CardService;
import com.card.service.MerchantService;
import com.card.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CardHandler extends WithAuthMerchantHandler {
    private static final Logger logger = LoggerFactory.getLogger(CardHandler.class);

    private final CardService cardService;

    public CardHandler(CardService cardService, TokenService tokenService, MerchantService merchantService) {
        super(tokenService, merchantService);
        this.cardService = cardService;
    }

    public Mono<ServerResponse> createVirtual(ServerRequest request) {
        return validateToken(request).flatMap(merchant -> request.bodyToMono(CreateCardDto.class).doOnSuccess(it -> {
            logger.info("Create virtual method was called with params:");
            logger.info(it.toString());
        }).map(this::toCard).flatMap(cardService::createVirtual)
                .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res)));
    }

    public Mono<ServerResponse> deposit(ServerRequest request) {
        return validateToken(request).flatMap(merchant->request.bodyToMono(CreateCardTransactionDto.class).doOnSuccess(it -> {
            logger.info("Deposit method was called with params:");
            logger.info(it.toString());
        })
                .flatMap(it -> cardService.deposit(it.getCardId(), it.getAmount(), it.getOrderId()))
                .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                .doOnError(e -> logger.error(e.getMessage(), e))
                .onErrorResume(e -> ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorDto(e.getMessage()))));
    }

    public Mono<ServerResponse> withdraw(ServerRequest request) {
        return validateToken(request).flatMap(merchant->request.bodyToMono(CreateCardTransactionDto.class).doOnSuccess(it -> {
            logger.info("Withdraw method was called with params:");
            logger.info(it.toString());
        })
                .flatMap(it -> cardService.withdraw(it.getCardId(), it.getAmount(), it.getOrderId()))
                .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res)));
    }

    private Card toCard(CreateCardDto createCardDto) {
        return new Card(CardType.VIRTUAL, createCardDto.getCustomerId(), createCardDto.getAccountId());
    }
}
