package com.card.service;

import com.card.entity.Customer;
import com.card.repository.CustomerRepository;
import com.card.service.exception.CustomerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> create(Customer customer) {
        return customerRepository.findByPhoneAndMerchantId(customer.getPhone(), customer.getMerchantId())
                .hasElement().flatMap(exist -> {
                    if (exist) return Mono.error(new CustomerException("Customer already exists"));
                    else return customerRepository.save(customer)
                            .doOnSuccess(it -> logger.info("Customer was created with id {}", it.getId()));
                });
    }

    public Mono<Customer> getById(Long id) {
        return customerRepository.findById(id);
    }
}
