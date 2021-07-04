package com.card.service;

import com.card.entity.Merchant;
import com.card.repository.MerchantRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Mono<Merchant> getById(Long id) {
        return merchantRepository.findById(id);
    }
}
