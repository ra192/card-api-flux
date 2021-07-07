package com.card.handler;

import com.card.dto.ErrorDto;
import com.card.entity.Customer;
import com.card.service.CustomerService;
import com.card.dto.CreateCustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CustomerHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomerHandler.class);

    private final CustomerService customerService;

    public CustomerHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        final var id = Long.parseLong(request.pathVariable("id"));
        return customerService.getById(id).flatMap(res ->
                ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CreateCustomerDto.class).doOnSuccess(it -> {
            logger.info("Create customer method was called with params:");
            logger.info(it.toString());
        }).map(this::toCustomer).flatMap(customerService::create)
                .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                .doOnError(e -> logger.error(e.getMessage(), e))
                .onErrorResume(e -> ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorDto(e.getMessage())));
    }

    private Customer toCustomer(CreateCustomerDto createCustomerDto) {
        final var customer = new Customer();
        BeanUtils.copyProperties(createCustomerDto, customer);
        customer.setActive(true);

        return customer;
    }
}
