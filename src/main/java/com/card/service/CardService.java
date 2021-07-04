package com.card.service;

import com.card.entity.Card;
import com.card.entity.enums.CardType;
import com.card.entity.enums.TransactionType;
import com.card.repository.CardRepository;
import com.card.service.dto.CreateCardDto;
import com.card.service.dto.CreateCardTransactionDto;
import com.card.service.dto.TransactionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CardService {
    private static final Logger logger= LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    public CardService(CardRepository cardRepository, TransactionService transactionService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
    }

    public Mono<Card> createVirtual(CreateCardDto cardDto) {
        logger.info("Create virtual card method was called with args:");
        logger.info(cardDto.toString());

        return cardRepository.save(new Card("xxxx", CardType.VIRTUAL, cardDto.getCustomerId(),
                cardDto.getAccountId(), "xxxx"));
    }

    public Mono<TransactionResultDto> deposit(CreateCardTransactionDto transactionDto) {
        logger.info("Deposit card method was called with args:");
        logger.info(transactionDto.toString());

        return cardRepository.findById(transactionDto.getCardId()).flatMap(card ->
                transactionService.withdraw(card.getAccountId(), transactionDto.getAmount(),
                        TransactionType.VIRTUAL_CARD_DEPOSIT, transactionDto.getOrderId(), transactionDto.getCardId()));
    }

    public Mono<TransactionResultDto> withdraw(CreateCardTransactionDto transactionDto) {
        logger.info("Withdraw card method was called with args:");
        logger.info(transactionDto.toString());

        return cardRepository.findById(transactionDto.getCardId()).flatMap(card ->
                transactionService.deposit(card.getAccountId(), transactionDto.getAmount(),
                        TransactionType.VIRTUAL_CARD_WITHDRAW, transactionDto.getOrderId(), transactionDto.getCardId()));
    }
}
