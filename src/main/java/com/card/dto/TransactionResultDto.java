package com.card.dto;

public class TransactionResultDto {
    private final Long transactionId;

    public TransactionResultDto(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}
