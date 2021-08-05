package com.card.service;

import com.card.entity.Account;
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

    private static final Long CASH_ACCOUNT_ID = 1L;

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final TransactionFeeRepository transactionFeeRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionItemRepository transactionItemRepository,
                              TransactionFeeRepository transactionFeeRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionFeeRepository = transactionFeeRepository;
        this.accountService = accountService;
    }

    public Mono<Transaction> deposit(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                     TransactionType type, String orderId, Long cardId) {
        return createTransactionWithFee(srcAccountId, destAccountId, feeAccountId, amount, type, orderId, cardId, true);
    }

    public Mono<Transaction> fund(Long accountId, Long amount, String orderId) {
        return createTransaction(CASH_ACCOUNT_ID, accountId, amount, TransactionType.FUND, orderId, null)
                .doOnSuccess(trans -> logger.info("Transaction {} was created", trans.getType()));
    }

    public Mono<Transaction> withdraw(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                      TransactionType type, String orderId, Long cardId) {
        return createTransactionWithFee(srcAccountId, destAccountId, feeAccountId, amount, type, orderId, cardId, false);
    }

    private Mono<Transaction> createTransaction(Long srcAccountId, Long destAccountId, Long amount, TransactionType type,
                                                String orderId, Long cardId) {
        return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED)).flatMap(trans ->
                transactionItemRepository.save(new TransactionItem(amount, trans.getId(), srcAccountId,
                        destAccountId, cardId)).map(it -> trans));
    }

    private Mono<Transaction> createTransactionWithFee(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                                       TransactionType type, String orderId, Long cardId, boolean isDeposit) {
        return sumByAccount(srcAccountId).zipWith(calculateFee(amount, type, destAccountId)).flatMap(zip -> {
            final var sum = zip.getT1();
            final var feeAmount = zip.getT2();

            if (sum - (isDeposit ? amount : amount + feeAmount) < 0)
                return Mono.error(new TransactionException("Account does not have enough funds"));

            final var transactionMono = createTransaction(srcAccountId, destAccountId, amount, type, orderId, cardId);
            if (feeAmount > 0)
                return transactionMono.flatMap(trans ->
                        transactionItemRepository.save(new TransactionItem(feeAmount, trans.getId(),
                                        isDeposit ? destAccountId : srcAccountId, feeAccountId, null))
                                .doOnSuccess(itm -> logger.info("Transaction {} was created", trans.getType()))
                                .map(itm -> trans));
            else
                return transactionMono.doOnSuccess(trans -> logger.info("Transaction {} was created", trans.getType()));

        });
    }

    private Mono<Long> calculateFee(Long amount, TransactionType type, Long accountId) {
        return transactionFeeRepository.findByTypeAndAccountId(type, accountId)
                .map(fee -> amount * fee.getRate().longValue()).defaultIfEmpty(0L);
    }

    private Mono<Long> sumByAccount(Long accountId) {
        return transactionItemRepository.findSumAmountBySrcAccountId(accountId).defaultIfEmpty(0L)
                .zipWith(transactionItemRepository.findSumAmountByDestAccountId(accountId).defaultIfEmpty(0L))
                .map(res -> res.getT2() - res.getT1());
    }
}
