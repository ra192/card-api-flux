package com.card.dto;

import com.card.entity.Transaction;
import com.card.entity.TransactionItem;

public class TransactionResultDto {
    private final Transaction transaction;
    private final TransactionItem baseItem;
    private final TransactionItem feeItem;

    public TransactionResultDto(Transaction transaction, TransactionItem baseItem, TransactionItem feeItem) {
        this.transaction = transaction;
        this.baseItem = baseItem;
        this.feeItem = feeItem;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public TransactionItem getBaseItem() {
        return baseItem;
    }

    public TransactionItem getFeeItem() {
        return feeItem;
    }
}
