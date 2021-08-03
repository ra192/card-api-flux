package com.card.dto;

public class FundAccountDto {
    private Long accountId;
    private Long amount;
    private String orderId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "FundAccountDto{" +
                "accountId=" + accountId +
                ", amount=" + amount +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
