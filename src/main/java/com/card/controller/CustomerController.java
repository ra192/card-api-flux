package com.card.controller;

import com.card.entity.Customer;
import com.card.service.CustomerService;
import com.card.service.dto.CreateCustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public Mono<Customer> create(@RequestBody CreateCustomerDto createDto) {
        return customerService.create(createDto);
    }
}
