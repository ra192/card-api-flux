package com.card.repository;

import com.card.entity.Card;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CardRepository extends ReactiveCrudRepository<Card, Long> {
}
