package com.card.entity;

import com.card.entity.enums.TransactionStatus;
import com.card.entity.enums.TransactionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Transaction {
    @Id
    private Long id;
    @Column("order_id")
    private String orderId;
    private TransactionType type;
    private TransactionStatus status;

    public Transaction(String orderId, TransactionType type, TransactionStatus status) {
        this.orderId = orderId;
        this.type = type;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
