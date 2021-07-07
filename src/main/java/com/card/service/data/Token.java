package com.card.service.data;

import java.time.LocalDateTime;

public class Token {
    private final String token;
    private final Long merchantId;
    private final LocalDateTime expiredAt;

    public Token(String token, Long merchantId, LocalDateTime expiredAt) {
        this.token = token;
        this.merchantId = merchantId;
        this.expiredAt = expiredAt;
    }

    public String getToken() {
        return token;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
