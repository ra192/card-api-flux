package com.card.service;

import com.card.entity.Merchant;
import com.card.service.data.Token;
import com.card.service.exception.TokenException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final Cache cache;
    private final int tokenSize;
    private final long tokenLifetimeInMinutes;
    private final MerchantService merchantService;

    public TokenService(CacheManager cacheManager,
                        @Value("${com.card.auth.token.size}") int tokenSize,
                        @Value("${com.card.auth.liftime.minutes}") long tokenLifetimeInMinutes, MerchantService merchantService) {
        cache = cacheManager.getCache("tokens");
        this.tokenSize = tokenSize;
        this.tokenLifetimeInMinutes = tokenLifetimeInMinutes;
        this.merchantService = merchantService;
    }

    public Mono<Token> create(Long merchantId, String secret) {
        return merchantService.getById(merchantId).flatMap(merchant -> {

            try {
                if (!merchant.getSecret().equalsIgnoreCase(sha256Hash(secret)))
                    return Mono.error(() -> new TokenException("Secret is not valid"));
            } catch (NoSuchAlgorithmException e) {
                return Mono.error(e);
            }

            final var tokenStr = RandomStringUtils.randomAlphanumeric(tokenSize);
            final var expiredAt = LocalDateTime.now().plusMinutes(tokenLifetimeInMinutes);

            final var token = new Token(tokenStr, merchant.getId(), expiredAt);
            cache.put(tokenStr, token);

            logger.info("Token was created");

            return Mono.just(token);

        });
    }

    public Token validate(String token) throws TokenException {
        final var cacheValue = cache.get(token);
        if (cacheValue == null) throw new TokenException("Token doesn't exist");
        final var tokenObj = (Token) cacheValue.get();

        assert tokenObj != null;
        if (LocalDateTime.now().isAfter(tokenObj.getExpiredAt())) throw new TokenException("Token is expired");

        return tokenObj;
    }

    private String sha256Hash(String text) throws NoSuchAlgorithmException {
        final var digest = MessageDigest.getInstance("SHA-256");
        return new String(Base64.getEncoder().encode(digest.digest(text.getBytes(StandardCharsets.UTF_8))),
                StandardCharsets.UTF_8);
    }
}
