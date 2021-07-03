package com.card.entity;

import com.card.entity.enums.TransactionItemType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public class TransactionItem {
    @Id
    private Long id;
    private Long amount;
    @Column("account_id")
    private Long accountId;
    private LocalDateTime created;
    private TransactionItemType type;
    @Column("card_id")
    private Long cardId;
    @Column("items_id")
    private Long transactionId;

    public TransactionItem(Long amount, Long transactionId, Long accountId, TransactionItemType type, Long cardId) {
        this.amount = amount;
        this.accountId = accountId;
        this.created = LocalDateTime.now();
        this.type = type;
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public TransactionItemType getType() {
        return type;
    }

    public void setType(TransactionItemType type) {
        this.type = type;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
