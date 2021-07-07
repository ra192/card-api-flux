package com.card;

import com.card.handler.CardHandler;
import com.card.handler.CustomerHandler;
import com.card.handler.TokenHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@EnableCaching
public class CardApiFlux {

    @Bean
    RouterFunction<ServerResponse> router(TokenHandler tokenHandler, CustomerHandler customerHandler, CardHandler cardHandler) {
        return route()
                .POST("/api/token", accept(MediaType.APPLICATION_JSON), tokenHandler::create)
                .GET("/api/customer/{id}", accept(MediaType.APPLICATION_JSON), customerHandler::getById)
                .POST("/api/customer", accept(MediaType.APPLICATION_JSON), customerHandler::create)
                .POST("/api/card", accept(MediaType.APPLICATION_JSON), cardHandler::createVirtual)
                .POST("/api/card/deposit", accept(MediaType.APPLICATION_JSON), cardHandler::deposit)
                .POST("/api/card/withdraw", accept(MediaType.APPLICATION_JSON), cardHandler::withdraw)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CardApiFlux.class, args);
    }
}
