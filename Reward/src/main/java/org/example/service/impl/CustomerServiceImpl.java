package org.example.service.impl;

import org.example.entity.Customer;
import org.example.repository.CustomerRepository;
import org.example.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getById(long customerId) {
        return customerRepository.getById(customerId);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
