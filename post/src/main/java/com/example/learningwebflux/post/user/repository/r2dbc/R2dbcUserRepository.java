package com.example.learningwebflux.post.user.repository.r2dbc;

import com.example.learningwebflux.post.user.User;
import com.example.learningwebflux.post.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
@Primary
public class R2dbcUserRepository implements UserRepository {
    public R2dbcUserRepository(
        @Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate,
        Mapper mapper
    ) {
        this.entityTemplate = entityTemplate;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> save(User user) {
        return entityTemplate.select(DataModel.class)
            .matching(query(where("id").is(user.id)))
            .one()
            .flatMap( existingEntity -> entityTemplate.update(mapper.toDataModel(user)) )
            .switchIfEmpty(entityTemplate.insert(mapper.toDataModel(user)))
            .then();
    }

    @Override
    public Mono<User> getWithId(String id) {
        return entityTemplate.select(DataModel.class)
            .matching(query(where("id").is(id)))
            .one()
            .map(mapper::toDomainModel);
    }

    private final R2dbcEntityTemplate entityTemplate;
    private final Mapper mapper;
}
