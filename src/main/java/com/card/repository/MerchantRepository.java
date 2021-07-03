package com.card.repository;

import com.card.entity.Merchant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MerchantRepository extends ReactiveCrudRepository<Merchant, Long> {
}
