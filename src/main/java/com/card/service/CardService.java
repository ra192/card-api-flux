package com.card.service;

import com.card.entity.Card;
import com.card.entity.Transaction;
import com.card.entity.enums.TransactionType;
import com.card.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private static final Long CARD_ACCOUNT_ID = 2L;
    private static final Long FEE_ACCOUNT_ID = 3L;


    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    public Mono<Card> createVirtual(Card card) {
        return cardRepository.save(card).doOnSuccess(it -> logger.info("Card was created with id: {}", it.getId()));
    }

    public Mono<Long> deposit(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card -> transactionService.withdraw(card.getAccountId(),
                CARD_ACCOUNT_ID, FEE_ACCOUNT_ID, amount, TransactionType.VIRTUAL_CARD_DEPOSIT, orderId, cardId))
                .map(Transaction::getId);
    }

    public Mono<Long> withdraw(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card -> transactionService.deposit(CARD_ACCOUNT_ID,
                card.getAccountId(), FEE_ACCOUNT_ID, amount, TransactionType.VIRTUAL_CARD_WITHDRAW, orderId, cardId))
                .map(Transaction::getId);
    }
}
