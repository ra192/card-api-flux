package com.card.dto;

import com.card.entity.Transaction;
import com.card.entity.TransactionItem;

public class TransactionResultDto {
    private final Transaction transaction;
    private final TransactionItem baseItem;
    private final TransactionItem feeIten;

    public TransactionResultDto(Transaction transaction, TransactionItem baseItem, TransactionItem feeIten) {
        this.transaction = transaction;
        this.baseItem = baseItem;
        this.feeIten = feeIten;
    }
}
