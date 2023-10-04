package org.example.service;

import org.example.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer getById(long customerId);
    List<Customer> findAll();
}
