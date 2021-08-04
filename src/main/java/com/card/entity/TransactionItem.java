package com.card.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public class TransactionItem {
    @Id
    private Long id;
    private Long amount;
    @Column("src_account_id")
    private Long srcAccountId;
    @Column("dest_account_id")
    private Long destAccountId;
    private LocalDateTime created;
    @Column("card_id")
    private Long cardId;
    @Column("items_id")
    private Long transactionId;

    public TransactionItem(Long amount, Long transactionId, Long srcAccountId, Long destAccountId, Long cardId) {
        this.amount = amount;
        this.transactionId=transactionId;
        this.srcAccountId = srcAccountId;
        this.destAccountId=destAccountId;
        this.created = LocalDateTime.now();
        this.cardId = cardId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getSrcAccountId() {
        return srcAccountId;
    }

    public void setSrcAccountId(Long srcAccountId) {
        this.srcAccountId = srcAccountId;
    }

    public Long getDestAccountId() {
        return destAccountId;
    }

    public void setDestAccountId(Long destAccountId) {
        this.destAccountId = destAccountId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}
