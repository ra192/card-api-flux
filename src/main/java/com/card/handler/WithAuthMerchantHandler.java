package com.card.handler;

import com.card.entity.Merchant;
import com.card.service.MerchantService;
import com.card.service.TokenService;
import com.card.service.exception.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public class WithAuthMerchantHandler {
    private static final Logger logger = LoggerFactory.getLogger(WithAuthMerchantHandler.class);
    private final TokenService tokenService;
    private final MerchantService merchantService;

    public WithAuthMerchantHandler(TokenService tokenService, MerchantService merchantService) {
        this.tokenService = tokenService;
        this.merchantService = merchantService;
    }

    protected Mono<Merchant> validateToken(ServerRequest request) {
        final var authHeader = request.headers().firstHeader("Authorization");
        logger.info("Authorization header: {}", authHeader);
        if (authHeader == null) return Mono.error(() -> new TokenException("Authorization header is not specified"));
        final var token = StringUtils.replace(authHeader, "Bearer", "").trim();
        try {
            return merchantService.getById(tokenService.validate(token).getMerchantId());
        } catch (TokenException e) {
            return Mono.error(e);
        }
    }
}
