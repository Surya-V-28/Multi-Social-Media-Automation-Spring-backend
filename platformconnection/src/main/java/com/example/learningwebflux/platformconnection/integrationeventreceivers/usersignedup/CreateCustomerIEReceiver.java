package com.example.learningwebflux.platformconnection.integrationeventreceivers.usersignedup;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE;
import com.example.learningwebflux.platformconnection.customer.Customer;
import com.example.learningwebflux.platformconnection.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CreateCustomerIEReceiver implements IntegrationEventReceiver<UserSignedUpIE> {
    public Mono<Void> receive(UserSignedUpIE event) {
        var customer = new Customer(event.id());
        return customerRepository.save(customer);
    }



    private final CustomerRepository customerRepository;
}