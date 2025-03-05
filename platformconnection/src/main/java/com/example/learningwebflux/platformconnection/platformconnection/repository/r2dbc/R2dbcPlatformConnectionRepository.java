package com.example.learningwebflux.platformconnection.platformconnection.repository.r2dbc;

import com.example.learningwebflux.platformconnection.Platform;
import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.platformconnection.r2dbc.datamodels.PlatformConnectionDataModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;

@Primary
@Component
public class R2dbcPlatformConnectionRepository implements PlatformConnectionRepository {
    public R2dbcPlatformConnectionRepository(
       @Qualifier("platformConnectionEntityTemplate") R2dbcEntityTemplate entityTemplate,
       Mapper mapper
    ) {
        this.entityTemplate = entityTemplate;
        this.mapper = mapper;
    }

    @Override
    public Mono<PlatformConnection> getWithId(String id) {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching(Query.query(where("id").is(id)))
            .one()
            .map(mapper::toDomainModel);
    }

    @Override
    public Mono<PlatformConnection> get(String userId, Platform platform, String platformUserId) {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching(Query.query(
                where("user_id").is(userId)
                    .and(where("platform").is(platform.name()))
                    .and(where("platform_user_id").is(platformUserId))
            ))
            .one()
            .map(mapper::toDomainModel);
    }

    @Override
    public Mono<List<PlatformConnection>> getRefreshables() {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching(Query.query( where("expires_at").lessThan(OffsetDateTime.now().plusDays(2)) ))
            .all()
            .map(mapper::toDomainModel)
            .collectList();
    }

    @Override
    public Mono<List<PlatformConnection>> getAllValid() {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching(Query.query( where("access_token").isNotNull() ))
            .all()
            .map(mapper::toDomainModel)
            .collectList();
    }

    @Override
    public Mono<Boolean> exists(String id) {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching( Query.query(where("id").is(id)) )
            .exists();
    }

    @Override
    public Mono<Boolean> exists(String userId, Platform platform, String platformUserId) {
        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching(Query.query(
                where("user_id").is(userId)
                    .and(where("platform").is(platform.name()))
                    .and(where("platform_user_id").is(platformUserId))
            ))
            .exists();
    }

    @Override
    public Mono<Void> save(PlatformConnection platformConnection) {
        var dataModel = mapper.toDataModel(platformConnection);

        return entityTemplate.select(PlatformConnectionDataModel.class)
            .matching( Query.query(where("id").is(platformConnection.id)) )
            .one()
            .flatMap(x -> entityTemplate.update(dataModel))
            .switchIfEmpty(entityTemplate.insert(dataModel))
            .then();
    }

    @Override
    public Mono<Void> remove(String id) {
        return entityTemplate.delete(PlatformConnectionDataModel.class)
            .matching(Query.query(where("id").is(id)))
            .all()
            .then();
    }


    private final Mapper mapper;
    private final R2dbcEntityTemplate entityTemplate;
}
