package com.card.controller;

import com.card.service.CardService;
import com.card.service.dto.TransactionResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/deposit")
    public Mono<TransactionResultDto> deposit(@RequestParam Long cardId, @RequestParam Long amount, @RequestParam String orderId) {
        return cardService.deposit(cardId, amount, orderId);
    }

    @GetMapping("/withdraw")
    public Mono<TransactionResultDto> withdraw(@RequestParam Long cardId, @RequestParam Long amount, @RequestParam String orderId) {
        return cardService.withdraw(cardId, amount, orderId);
    }
}
