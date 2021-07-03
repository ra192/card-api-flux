package com.card.repository;

import com.card.entity.TransactionFee;
import com.card.entity.enums.TransactionType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionFeeRepository extends ReactiveCrudRepository<TransactionFee, Long> {
    Mono<TransactionFee> findByTypeAndAccountId(TransactionType type, Long accountId);
}
