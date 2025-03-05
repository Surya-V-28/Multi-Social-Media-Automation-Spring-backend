package com.example.learningwebflux.post.user.repository.r2dbc;

import com.example.learningwebflux.post.user.User;
import org.springframework.stereotype.Component;

@Component
class Mapper {
    DataModel toDataModel(User domainModel) {
        return new DataModel(domainModel.id);
    }

    User toDomainModel(DataModel dataModel) {
        return new User(dataModel.id);
    }
}
