package com.example.learningwebflux.platformconnection.customer.repository.r2dbc;

import com.example.learningwebflux.platformconnection.customer.Customer;
import org.springframework.stereotype.Component;

@Component
class Mapper {
    public DataModel toDataModel(Customer domainModel) {
        return new DataModel(domainModel.id);
    }

    public Customer toDomainModel(DataModel dataModel) {
        return new Customer(dataModel.id);
    }
}
