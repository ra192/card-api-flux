package com.card.handler;

import com.card.dto.CreateCardTransactionDto;
import com.card.dto.ErrorDto;
import com.card.dto.FundAccountDto;
import com.card.service.AccountService;
import com.card.service.MerchantService;
import com.card.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AccountHandler extends WithAuthMerchantHandler {
    private static final Logger logger= LoggerFactory.getLogger(AccountHandler.class);

    private static final Long INTERNAL_MERCHANT_ID = 1L;

    private final AccountService accountService;

    public AccountHandler(TokenService tokenService, MerchantService merchantService, AccountService accountService) {
        super(tokenService, merchantService);
        this.accountService = accountService;
    }

    public Mono<ServerResponse> fund(ServerRequest request) {
        return validateToken(request).flatMap(merchant-> {
            if(!merchant.getId().equals(INTERNAL_MERCHANT_ID)) {
                return Mono.error(new Exception("Internal merchant required"));
            }
            return request.bodyToMono(FundAccountDto.class).doOnSuccess(it -> {
                        logger.info("Fund method was called with params:");
                        logger.info(it.toString());
                    })
                    .flatMap(it -> accountService.fund(it.getAccountId(), it.getAmount(), it.getOrderId()))
                    .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res))
                    .doOnError(e -> logger.error(e.getMessage(), e))
                    .onErrorResume(e -> ok().contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorDto(e.getMessage())));
        });
    }
}
