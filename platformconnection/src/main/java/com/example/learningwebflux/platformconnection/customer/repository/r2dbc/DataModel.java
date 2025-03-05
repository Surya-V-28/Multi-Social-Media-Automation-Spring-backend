package com.example.learningwebflux.platformconnection.customer.repository.r2dbc;

import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
@AllArgsConstructor
class DataModel {
    public final String id;
}
