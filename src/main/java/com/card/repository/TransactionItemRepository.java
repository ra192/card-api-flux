package com.card.repository;

import com.card.entity.TransactionItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionItemRepository extends ReactiveCrudRepository<TransactionItem, Long> {
    Mono<Long> findSumAmountBySrcAccountId(Long accountId);
    Mono<Long> findSumAmountByDestAccountId(Long accountId);
}
