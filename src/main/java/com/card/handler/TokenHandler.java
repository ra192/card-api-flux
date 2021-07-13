package com.card.handler;

import com.card.dto.CreateTokenDto;
import com.card.dto.ErrorDto;
import com.card.dto.TokenDto;
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
public class TokenHandler {
    private static final Logger logger = LoggerFactory.getLogger(TokenHandler.class);

    private final TokenService tokenService;

    public TokenHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CreateTokenDto.class).flatMap(it -> tokenService.create(it.getMerchantId(), it.getSecret()))
                .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(new TokenDto(res)))
                .doOnError(e -> logger.error(e.getMessage(), e))
                .onErrorResume(e -> ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorDto(e.getMessage())));
    }
}
