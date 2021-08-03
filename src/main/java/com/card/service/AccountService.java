package com.card.service;

import com.card.entity.Account;
import com.card.entity.Transaction;
import com.card.entity.enums.TransactionType;
import com.card.repository.AccountRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountService {
    private static final Long CASH_ACCOUNT_ID = 1L;

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public Mono<Account> findActiveById(Long id) {
        return accountRepository.findByIdAndActive(id, true);
    }

    public Mono<Transaction> fund(Long accountId, Long amount, String orderId) {
        return transactionService.fund(CASH_ACCOUNT_ID, accountId, amount, TransactionType.FUND, orderId);
    }
}
