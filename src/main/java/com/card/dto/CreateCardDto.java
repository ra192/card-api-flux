package com.card.dto;

public class CreateCardDto {
    private Long customerId;
    private Long accountId;

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

    @Override
    public String toString() {
        return "CreateCardDto{" +
                "customerId=" + customerId +
                ", accountId=" + accountId +
                '}';
    }
}
