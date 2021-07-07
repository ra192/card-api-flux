package com.card.dto;

public class CreateCardTransactionDto {
    private Long cardId;
    private Long amount;
    private String orderId;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
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
        return "CreateCardTransactionDto{" +
                "cardId=" + cardId +
                ", amount=" + amount +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
