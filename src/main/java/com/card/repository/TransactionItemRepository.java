package com.card.repository;

import com.card.entity.TransactionItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionItemRepository extends ReactiveCrudRepository<TransactionItem, Long> {
    @Query("select sum (amount) from transaction_item where src_account_id = $1")
    Mono<Long> sumBySrcAccountId(Long accountId);

    @Query("select sum (amount) from transaction_item itm where dest_account_id = $1")
    Mono<Long> sumByDestAccountId(Long accountId);
}
