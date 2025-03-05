package com.example.learningwebflux.post.restapi.routes.getscheduledpost;

import com.example.learningwebflux.post.r2dbc.models.ScheduledPostDataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class GetScheduledPostRouteHandler {
    public GetScheduledPostRouteHandler(
        @Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate,
        ObjectMapper objectMapper
    ) {
        this.entityTemplate = entityTemplate;
        this.objectMapper = objectMapper;
    }


    public Mono<ServerResponse> handle(ServerRequest request) {
        var criteria = buildCriteria(request.pathVariable("userId"), request.pathVariable("id"));

        return entityTemplate.select(ScheduledPostDataModel.class)
            .matching(query(criteria))
            .one()
            .flatMap(this::createScheduledPostResponseBody)
            .flatMap(responseBody -> {
                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            });
    }

    private Mono<JsonNode> createScheduledPostResponseBody(ScheduledPostDataModel dataModel) {
        JsonNode mediasResponseBody;
        try { mediasResponseBody = objectMapper.readValue(dataModel.medias.asString(), JsonNode.class); }
        catch (JsonProcessingException exception) { return Mono.error(exception); }

        JsonNode targetsResponseBody;
        try { targetsResponseBody = objectMapper.readValue(dataModel.targets.asString(), JsonNode.class); }
        catch (JsonProcessingException exception) { return Mono.error(exception); }

        var scheduledPostResponseBody = JsonNodeFactory.instance.objectNode();
        scheduledPostResponseBody.put("id", dataModel.id);
        scheduledPostResponseBody.put("userId", dataModel.userId);
        scheduledPostResponseBody.put("scheduledTime", dataModel.scheduledTime.toString());
        scheduledPostResponseBody.put("isFulfilled", dataModel.isFulfilled);

        scheduledPostResponseBody.put("title", dataModel.title);
        scheduledPostResponseBody.put("caption", dataModel.caption);
        scheduledPostResponseBody.set("medias", mediasResponseBody);
        scheduledPostResponseBody.set("targets", targetsResponseBody);


        return Mono.just(scheduledPostResponseBody);
    }

    private Criteria buildCriteria(String userId, String id) {
        return where("user_id").is(userId)
            .and(where("id").is(id));
    }


    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;
}