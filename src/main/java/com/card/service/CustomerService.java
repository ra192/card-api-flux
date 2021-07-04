package com.card.service;

import com.card.entity.Customer;
import com.card.repository.CustomerRepository;
import com.card.service.dto.CreateCustomerDto;
import com.card.service.exception.CustomerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> create(CreateCustomerDto customerDto) {
        logger.info("Create customer method was called with args:");
        logger.info(customerDto.toString());

        return customerRepository.findByPhoneAndMerchantId(customerDto.getPhone(), customerDto.getMerchantId())
                .hasElement().flatMap(exist -> {
                    if (exist) {
                        final var errorText = "Customer already exists";
                        logger.error(errorText);
                        return Mono.error(new CustomerException(errorText));
                    } else {
                        final var customer = new Customer();
                        BeanUtils.copyProperties(customerDto, customer);
                        customer.setActive(true);
                        return customerRepository.save(customer);
                    }

                });
    }

    public Mono<Customer> getById(Long id) {
        return customerRepository.findById(id);
    }
}
