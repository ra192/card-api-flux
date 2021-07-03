package com.card.entity;

import com.card.entity.enums.CardType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;


public class Card {
    @Id
    private Long id;
    @Column("provider_reference_id")
    private String providerReferenceId;
    private CardType type;
    @Column("customer_id")
    private Long customerId;
    @Column("account_id")
    private Long accountId;
    private LocalDate created;
    private String info;

    public Card(String providerReferenceId, CardType type, Long customerId, Long accountId, String info) {
        this.providerReferenceId = providerReferenceId;
        this.type = type;
        this.customerId = customerId;
        this.accountId = accountId;
        this.created = LocalDate.now();
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderReferenceId() {
        return providerReferenceId;
    }

    public void setProviderReferenceId(String providerReferenceId) {
        this.providerReferenceId = providerReferenceId;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
