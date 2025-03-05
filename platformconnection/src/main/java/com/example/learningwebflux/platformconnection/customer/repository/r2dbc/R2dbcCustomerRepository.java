package com.example.learningwebflux.platformconnection.customer.repository.r2dbc;

import com.example.learningwebflux.platformconnection.customer.Customer;
import com.example.learningwebflux.platformconnection.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Primary
@Component
public class R2dbcCustomerRepository implements CustomerRepository {
    public R2dbcCustomerRepository(
        @Qualifier("platformConnectionEntityTemplate") R2dbcEntityTemplate entityTemplate,
        Mapper mapper
    ) {
        this.entityTemplate = entityTemplate;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> save(Customer customer) {
        return entityTemplate.select(DataModel.class)
            .matching(query(where("id").is(customer.id)))
            .one()
            .flatMap( dataModel -> entityTemplate.update(mapper.toDataModel(customer)) )
            .switchIfEmpty(entityTemplate.insert(mapper.toDataModel(customer)))
            .then();
    }

    @Override
    public Mono<Customer> withId(String id) {
        return null;
    }


    R2dbcEntityTemplate entityTemplate;
    private final Mapper mapper;
}
