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

    public Mono<Transaction> deposit(Long srcAccountId, Long destAccountId, Long destFeeAccountId, Long amount,
                                     TransactionType type, String orderId, Long cardId) {
        return createTransaction(srcAccountId, destAccountId, destFeeAccountId, amount, type,
                orderId, cardId, true);

    }

    public Mono<Transaction> withdraw(Long srcAccountId, Long destAccountId, Long destFeeAccountId, Long amount,
                                      TransactionType type, String orderId, Long cardId) {
        return createTransaction(srcAccountId, destAccountId, destFeeAccountId, amount, type,
                orderId, cardId, false);
    }

    private Mono<Transaction> createTransaction(Long srcAccountId, Long destAccountId, Long destFeeAccountId, Long amount, TransactionType type,
                                                String orderId, Long cardId, boolean isDeposit) {
        return sumByAccount(srcAccountId).flatMap(sum -> {
            final var srcFeeAccountId = isDeposit ? destAccountId : srcAccountId;
            return transactionFeeRepository.findByTypeAndAccountId(type, srcFeeAccountId).flatMap(fee -> {
                final var feeAmount = fee.getRate().longValue() * amount;
                final var srcTotalAmount = isDeposit ? amount : amount + feeAmount;
                if (sum - srcTotalAmount < 0)
                    return Mono.error(new TransactionException("Account does not have enough funds"));

                return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED)).flatMap(trans ->
                        transactionItemRepository.save(new TransactionItem(amount, trans.getId(), srcAccountId,
                                destAccountId, cardId)).flatMap(baseItm ->
                                transactionItemRepository.save(
                                        new TransactionItem(feeAmount, trans.getId(), srcFeeAccountId, destFeeAccountId,
                                                null))).map(it -> trans));
            }).switchIfEmpty(Mono.defer(() -> {
                if (sum - amount < 0)
                    return Mono.error(new TransactionException("Account does not have enough funds"));

                return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED)).flatMap(
                        trans -> transactionItemRepository.save(new TransactionItem(amount, trans.getId(),
                                srcAccountId, destAccountId, cardId)).map(it -> trans));
            }));
        });
    }

    private Mono<Long> sumByAccount(Long accountId) {
        return transactionItemRepository.findSumAmountBySrcAccountId(accountId).defaultIfEmpty(0L)
                .flatMap(srcAmount -> transactionItemRepository.findSumAmountByDestAccountId(accountId)
                        .defaultIfEmpty(0L).map(destAmount -> destAmount - srcAmount));
    }
}
