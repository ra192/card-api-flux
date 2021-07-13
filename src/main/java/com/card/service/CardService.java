package com.card.service;

import com.card.entity.Card;
import com.card.entity.enums.TransactionType;
import com.card.repository.CardRepository;
import com.card.dto.TransactionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    public Mono<Card> createVirtual(Card card) {
        return cardRepository.save(card).doOnSuccess(it -> logger.info("Card was created with id: {}", it.getId()));
    }

    public Mono<TransactionResultDto> deposit(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card -> transactionService.withdraw(card.getAccountId(), amount,
                TransactionType.VIRTUAL_CARD_DEPOSIT, orderId, cardId));
    }

    public Mono<TransactionResultDto> withdraw(Long cardId, Long amount, String orderId) {
        return cardRepository.findById(cardId).flatMap(card -> transactionService.deposit(card.getAccountId(), amount,
                TransactionType.VIRTUAL_CARD_WITHDRAW, orderId, cardId));
    }
}
