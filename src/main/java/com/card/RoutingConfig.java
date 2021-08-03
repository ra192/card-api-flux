package com.card;

import com.card.handler.AccountHandler;
import com.card.handler.CardHandler;
import com.card.handler.CustomerHandler;
import com.card.handler.TokenHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutingConfig {
    @Bean
    RouterFunction<ServerResponse> router(TokenHandler tokenHandler, AccountHandler accountHandler, CustomerHandler customerHandler, CardHandler cardHandler) {
        return route()
                .POST("/api/token", accept(MediaType.APPLICATION_JSON), tokenHandler::create)
                .POST("/api/account/fund", accept(MediaType.APPLICATION_JSON), accountHandler::fund)
                .POST("/api/customer", accept(MediaType.APPLICATION_JSON), customerHandler::create)
                .POST("/api/card", accept(MediaType.APPLICATION_JSON), cardHandler::createVirtual)
                .POST("/api/card/deposit", accept(MediaType.APPLICATION_JSON), cardHandler::deposit)
                .POST("/api/card/withdraw", accept(MediaType.APPLICATION_JSON), cardHandler::withdraw)
                .build();
    }
}
