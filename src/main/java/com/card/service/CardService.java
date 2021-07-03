package com.card.service;

import com.card.entity.Card;
import com.card.entity.enums.CardType;
import com.card.entity.enums.TransactionType;
import com.card.repository.CardRepository;
import com.card.service.dto.TransactionResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    @Autowired
    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    public Mono<Card> createVirtual(Long customerId, Long accountId) {
        return cardRepository.save(new Card("xxxx", CardType.VIRTUAL, customerId, accountId, "xxxx"));
    }

    public Mono<TransactionResultDto> deposit(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card ->
                transactionService.withdraw(card.getAccountId(), amount, TransactionType.VIRTUAL_CARD_DEPOSIT, orderId, cardId));
    }

    public Mono<TransactionResultDto> withdraw(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card ->
                transactionService.deposit(card.getAccountId(), amount, TransactionType.VIRTUAL_CARD_WITHDRAW, orderId, cardId));
    }
}
