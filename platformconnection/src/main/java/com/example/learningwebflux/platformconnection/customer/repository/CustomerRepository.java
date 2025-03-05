package com.example.learningwebflux.platformconnection.customer.repository;

import com.example.learningwebflux.platformconnection.customer.Customer;
import reactor.core.publisher.Mono;

public interface CustomerRepository {
    Mono<Void> save(Customer customer);
    Mono<Customer> withId(String id);
}
