package com.example.learningwebflux.post.platformconnection.repository.r2dbc;

import com.example.learningwebflux.post.platformconnection.PlatformConnection;
import com.example.learningwebflux.post.platformconnection.repository.PlatformConnectionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Primary
@Component
public class R2dbcPlatformConnectionRepository implements PlatformConnectionRepository {
    public R2dbcPlatformConnectionRepository(
        @Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate
    ) {
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<PlatformConnection> withId(String id) {
        return entityTemplate.select(PlatformConnection.class)
            .matching(query(where("id").is(id)))
            .one();
    }

    @Override
    public Mono<List<PlatformConnection>> ofUser(String userId) {
        return entityTemplate.select(PlatformConnection.class)
            .matching(query(where("user_id").is(userId)))
            .all()
            .collectList();
    }

    @Override
    public Mono<Void> save(PlatformConnection platformConnection) {
        return entityTemplate.select(PlatformConnection.class)
            .matching(query(where("id").is(platformConnection.id)))
            .one()
            .flatMap(dataModel -> entityTemplate.update(platformConnection))
            .switchIfEmpty(entityTemplate.insert(platformConnection))
            .then();
    }


    private final R2dbcEntityTemplate entityTemplate;
}