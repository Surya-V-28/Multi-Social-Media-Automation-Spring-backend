package com.example.learningwebflux.post.scheduledpost.repository.r2dbc;

import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import com.example.learningwebflux.post.r2dbc.models.ScheduledPostDataModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
@Primary
public class R2dbcScheduledPostRepository implements ScheduledPostRepository {
    public R2dbcScheduledPostRepository(
        @Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate,
        Mapper mapper
    ) {
        this.entityTemplate = entityTemplate;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> save(ScheduledPost post) {
        ScheduledPostDataModel dataModel;
        try { dataModel = mapper.toDataModel(post); }
        catch (Exception exception) { return Mono.error(exception); }

        return entityTemplate.select(ScheduledPostDataModel.class)
            .matching(query(where("id").is(post.id)))
            .one()
            .flatMap(entity -> {
                try { return entityTemplate.update(dataModel); }
                catch (Exception exception) { return Mono.error(exception); }
            })
            .switchIfEmpty(entityTemplate.insert(dataModel))
            .then();
    }

    @Override
    public Mono<ScheduledPost> getWithId(String id) {
        return entityTemplate.select(ScheduledPostDataModel.class)
            .matching(query(where("id").is(id)))
            .one()
            .flatMap(dataModel -> {
                try { return Mono.just(mapper.toDomainModel(dataModel)); }
                catch (Exception exception) { return Mono.error(exception); }
            });
    }

    @Override
    public Mono<List<ScheduledPost>> getOfUser(String userId) {
        return entityTemplate.select(ScheduledPostDataModel.class)
            .matching(query(where("user_id").is(userId)))
            .all()
            .flatMap(dataModel -> {
                try { return Mono.just(mapper.toDomainModel(dataModel)); }
                catch(Exception exception) { return Mono.error(exception); }
            })
            .collectList();
    }

    @Override
    public Mono<Void> removeWithId(String id) {
        return entityTemplate.delete(ScheduledPostDataModel.class)
            .matching(query(where("id").is(id)))
            .all()
            .then();
    }


    private final R2dbcEntityTemplate entityTemplate;
    private final Mapper mapper;
}
