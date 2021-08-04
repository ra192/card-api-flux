package com.card.service;

import com.card.entity.Account;
import com.card.repository.AccountRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Mono<Account> findActiveById(Long id) {
        return accountRepository.findByIdAndActive(id, true);
    }

    protected void save(Account account) {
        accountRepository.save(account);
    }
}
