package org.example.service.impl;

import org.example.repository.AccountRepository;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean isEmpty(Long customerId) {
        return isNull(customerId) || accountRepository.findByCustomerId(customerId).isEmpty();
    }
}
