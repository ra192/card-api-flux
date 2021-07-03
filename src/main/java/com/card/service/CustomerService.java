package com.card.service;

import com.card.entity.Customer;
import com.card.repository.CustomerRepository;
import com.card.service.dto.CreateCustomerDto;
import com.card.service.exception.CustomerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> create(CreateCustomerDto customerDto) {
        return customerRepository.findByPhoneAndMerchantId(customerDto.getPhone(),customerDto.getMerchantId())
                .hasElement().flatMap(exist->{
                    if(exist) return Mono.error(new CustomerException("Customer already exists"));
                    else {
                        final var customer = new Customer();
                        BeanUtils.copyProperties(customerDto, customer);
                        customer.setActive(true);
                        return customerRepository.save(customer);
                    }

        });
    }
}
