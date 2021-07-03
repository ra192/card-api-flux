package com.card.repository;

import com.card.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Mono<Customer>findByPhoneAndMerchantId(String phone, Long merchantId);
}
