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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
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
        return accountService.findActiveById(srcAccountId).zipWith(accountService.findActiveById(destAccountId))
                .zipWith(accountService.findActiveById(feeAccountId)).flatMap(accs -> {
                    final var srcAccount = accs.getT1().getT1();
                    final var destAccount = accs.getT1().getT2();
                    final var feeAccount = accs.getT2();

                    return calculateFee(amount, type, destAccountId).flatMap(feeAmount -> {
                        if (srcAccount.getBalance() - amount < 0)
                            return Mono.error(new TransactionException("Account does not have enough funds"));

                        final var transactionMono = createTransaction(srcAccountId, destAccountId, amount, type, orderId, cardId);
                        if (feeAmount > 0)
                            return transactionMono.flatMap(trans ->
                                    transactionItemRepository.save(new TransactionItem(feeAmount, trans.getId(),
                                            destAccountId, feeAccountId, null)).map(it -> trans)).doOnSuccess(it -> {
                                updateBalance(srcAccount, -amount);
                                updateBalance(destAccount, amount - feeAmount);
                                updateBalance(feeAccount, feeAmount);
                            });
                        else {
                            return transactionMono.doOnSuccess(it -> {
                                updateBalance(srcAccount, -amount);
                                updateBalance(destAccount, amount);
                            });
                        }
                    });
                });
    }

    public Mono<Transaction> fund(Long accountId, Long amount, String orderId) {
        return createTransaction(CASH_ACCOUNT_ID, accountId, amount, TransactionType.FUND, orderId, null);
    }

    public Mono<Transaction> withdraw(Long srcAccountId, Long destAccountId, Long feeAccountId, Long amount,
                                      TransactionType type, String orderId, Long cardId) {
        return accountService.findActiveById(srcAccountId).zipWith(accountService.findActiveById(destAccountId))
                .zipWith(accountService.findActiveById(feeAccountId)).flatMap(accs -> {
                    final var srcAccount = accs.getT1().getT1();
                    final var destAccount = accs.getT1().getT2();
                    final var feeAccount = accs.getT2();

                    return calculateFee(amount, type, srcAccountId).flatMap(feeAmount -> {
                        if (srcAccount.getBalance() - amount - feeAmount < 0)
                            return Mono.error(new TransactionException("Account does not have enough funds"));

                        final var transactionMono = createTransaction(srcAccountId, destAccountId, amount, type, orderId, cardId);
                        if (feeAmount > 0)
                            return transactionMono.flatMap(trans ->
                                            transactionItemRepository.save(new TransactionItem(feeAmount, trans.getId(),
                                                    srcAccountId, feeAccountId, null)).map(it -> trans))
                                    .doOnSuccess(it -> {
                                        updateBalance(srcAccount, -amount - feeAmount);
                                        updateBalance(destAccount, amount);
                                        updateBalance(feeAccount, feeAmount);
                                    });
                        else
                            return transactionMono.doOnSuccess(it -> {
                                updateBalance(srcAccount, -amount);
                                updateBalance(destAccount, amount);
                            });
                    });
                });
    }

    private Mono<Transaction> createTransaction(Long srcAccountId, Long destAccountId, Long amount, TransactionType type,
                                                String orderId, Long cardId) {
        return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED)).flatMap(trans ->
                transactionItemRepository.save(new TransactionItem(amount, trans.getId(), srcAccountId,
                        destAccountId, cardId)).map(it -> trans));
    }

    private Mono<Long> calculateFee(Long amount, TransactionType type, Long accountId) {
        return transactionFeeRepository.findByTypeAndAccountId(type, accountId)
                .map(fee -> amount * fee.getRate().longValue()).defaultIfEmpty(0L);
    }

    private void updateBalance(Account account, Long amount) {
        account.setBalance(account.getBalance() + amount);
        accountService.save(account);
    }
}
