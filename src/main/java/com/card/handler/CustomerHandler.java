package com.card.handler;

import com.card.service.CustomerService;
import com.card.service.dto.CreateCustomerDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CustomerHandler {
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

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(CreateCustomerDto.class).flatMap(customerService::create).flatMap(res ->
                ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                .onErrorResume(e->ok().contentType(MediaType.APPLICATION_JSON).bodyValue(e.getMessage()));
    }
}
