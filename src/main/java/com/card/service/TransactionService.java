package com.card.service;

import com.card.entity.Transaction;
import com.card.entity.TransactionItem;
import com.card.entity.enums.TransactionStatus;
import com.card.entity.enums.TransactionType;
import com.card.repository.TransactionFeeRepository;
import com.card.repository.TransactionItemRepository;
import com.card.repository.TransactionRepository;
import com.card.service.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final TransactionFeeRepository transactionFeeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionItemRepository transactionItemRepository,
                              TransactionFeeRepository transactionFeeRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionFeeRepository = transactionFeeRepository;
    }

    public Mono<Transaction> deposit(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                     TransactionType type, String orderId, Long cardId) {
        return createTransaction(orderId, type).flatMap(
                trans -> createBaseItem(srcAccountId, destAccountId, amount, trans.getId(), cardId).flatMap(baseItm ->
                        createFeeItem(destAccountId, feeAccountId, amount, type, trans.getId())
                                .map(it -> trans)));
    }

    public Mono<Transaction> withdraw(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                      TransactionType type, String orderId, Long cardId) {
        return sumByAccount(srcAccountId).flatMap(sum -> {
            if (sum - amount < 0) {
                final var errorText = "Account does not have enough funds";
                logger.error(errorText);
                return Mono.error(new TransactionException(errorText));
            }
            return createTransaction(orderId, type).flatMap(
                    trans -> createBaseItem(srcAccountId, destAccountId, amount, trans.getId(), cardId).flatMap(baseItm ->
                            createFeeItem(srcAccountId, feeAccountId, amount, type, trans.getId())
                                    .map(it -> trans)));
        });
    }

    private Mono<Transaction> createTransaction(String orderId, TransactionType type) {
        return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED));
    }

    private Mono<TransactionItem> createBaseItem(Long srcAccountId, Long destAccountId, Long amount, Long transactionId,
                                                 Long cardId) {
        return transactionItemRepository.save(new TransactionItem(amount, transactionId, srcAccountId, destAccountId,
                cardId));
    }

    private Mono<TransactionItem> createFeeItem(Long srcAccountId, Long destAccountId, Long amount, TransactionType type, Long transactionId) {
        return transactionFeeRepository.findByTypeAndAccountId(type, srcAccountId).flatMap(fee ->
                transactionItemRepository.save(new TransactionItem(amount * fee.getRate().longValue(),
                        transactionId, srcAccountId, destAccountId, null)));
    }

    private Mono<Long> sumByAccount(Long accountId) {
        return transactionItemRepository.findSumAmountBySrcAccountId(accountId).defaultIfEmpty(0L)
                .flatMap(srcAmount -> transactionItemRepository.findSumAmountByDestAccountId(accountId)
                        .defaultIfEmpty(0L).map(destAmount -> destAmount - srcAmount));
    }
}
