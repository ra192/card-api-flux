package com.card.service;

import com.card.entity.Transaction;
import com.card.entity.TransactionItem;
import com.card.entity.enums.TransactionStatus;
import com.card.entity.enums.TransactionType;
import com.card.repository.TransactionFeeRepository;
import com.card.repository.TransactionItemRepository;
import com.card.repository.TransactionRepository;
import com.card.service.exception.TransactionException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
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
        return sumByAccount(srcAccountId).flatMap(sum ->
                calculateFee(amount, type, destAccountId).flatMap(feeAmount -> {
                    if (sum - amount < 0)
                        return Mono.error(new TransactionException("Account does not have enough funds"));

                    final var transactionMono = createTransaction(srcAccountId, destAccountId, amount, type, orderId, cardId);
                    if (feeAmount > 0)
                        return transactionMono.flatMap(trans ->
                                transactionItemRepository.save(new TransactionItem(feeAmount, trans.getId(),
                                        destAccountId, feeAccountId, null)).map(it -> trans));
                    else
                        return transactionMono;

                }));
    }

    public Mono<Transaction> fund(Long srcAccountId, Long destAccountId, Long amount, TransactionType type,
                                  String orderId) {
        return createTransaction(srcAccountId,destAccountId,amount,type,orderId,null);
    }

    public Mono<Transaction> withdraw(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                      TransactionType type, String orderId, Long cardId) {
        return sumByAccount(srcAccountId).flatMap(sum ->
                calculateFee(amount, type, srcAccountId).flatMap(feeAmount -> {
                    if (sum - amount -feeAmount < 0)
                        return Mono.error(new TransactionException("Account does not have enough funds"));

                    final var transactionMono = createTransaction(srcAccountId, destAccountId, amount, type, orderId, cardId);
                    if (feeAmount > 0)
                        return transactionMono.flatMap(trans ->
                                transactionItemRepository.save(new TransactionItem(feeAmount, trans.getId(),
                                        srcAccountId, feeAccountId, null)).map(it -> trans));
                    else
                        return transactionMono;

                }));
    }

    private Mono<Transaction> createTransaction(Long srcAccountId, Long destAccountId, Long amount, TransactionType type,
                                                String orderId, Long cardId) {
        return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED)).flatMap(trans ->
                transactionItemRepository.save(new TransactionItem(amount, trans.getId(), srcAccountId,
                        destAccountId, cardId)).map(it -> trans));
    }

    private Mono<Long> sumByAccount(Long accountId) {
        return transactionItemRepository.findSumAmountBySrcAccountId(accountId).defaultIfEmpty(0L)
                .flatMap(srcAmount -> transactionItemRepository.findSumAmountByDestAccountId(accountId)
                        .defaultIfEmpty(0L).map(destAmount -> destAmount - srcAmount));
    }

    private Mono<Long> calculateFee(Long amount, TransactionType type, Long accountId) {
        return transactionFeeRepository.findByTypeAndAccountId(type, accountId)
                .map(fee -> amount * fee.getRate().longValue()).defaultIfEmpty(0L);
    }
}
