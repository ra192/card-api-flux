package com.card.service;

import com.card.entity.Transaction;
import com.card.entity.TransactionItem;
import com.card.entity.enums.TransactionItemType;
import com.card.entity.enums.TransactionStatus;
import com.card.entity.enums.TransactionType;
import com.card.repository.TransactionFeeRepository;
import com.card.repository.TransactionItemRepository;
import com.card.repository.TransactionRepository;
import com.card.service.dto.TransactionResultDto;
import com.card.service.exception.TransactionException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final TransactionFeeRepository transactionFeeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionItemRepository transactionItemRepository,
                              TransactionFeeRepository transactionFeeRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionFeeRepository = transactionFeeRepository;
    }

    public Mono<TransactionResultDto> deposit(Long accountId, Long amount, TransactionType type, String orderId, Long cardId) {
        return createTransactionWithItems(accountId, amount, type, TransactionItemType.DEPOSIT, orderId, cardId);
    }

    public Mono<TransactionResultDto> withdraw(Long accountId, Long amount, TransactionType type, String orderId, Long cardId) {
        return transactionItemRepository.sumByAccount(accountId).flatMap(sum -> (sum - amount < 0) ?
                Mono.error(new TransactionException("Account does not have enough funds")) :
                createTransactionWithItems(accountId, -amount, type, TransactionItemType.WITHDRAW, orderId, cardId));
    }

    private Mono<TransactionResultDto> createTransactionWithItems(Long accountId, Long amount, TransactionType type, TransactionItemType baseItemType, String orderId, Long cardId) {
        return transactionRepository.save(new Transaction(orderId, type, TransactionStatus.COMPLETED))
                .flatMap(transaction ->
                        transactionItemRepository.save(new TransactionItem(amount, transaction.getId(), accountId,
                                baseItemType, cardId))
                                .flatMap(baseItem -> transactionFeeRepository.findByTypeAndAccountId(type, accountId)
                                        .flatMap(transactionFee ->
                                                transactionItemRepository.save(
                                                        new TransactionItem((amount > 0) ? -amount : amount * transactionFee.getRate().longValue(),
                                                                transaction.getId(), accountId, TransactionItemType.FEE, null))
                                                        .map(feeItem -> new TransactionResultDto(transaction, baseItem, feeItem)))));
    }
}
