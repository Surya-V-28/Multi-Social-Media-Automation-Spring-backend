package com.example.learningwebflux.platformconnection.restapi.routes.getplatformconnection;

import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class GetPlatformConnectionRouteHandler {
    public GetPlatformConnectionRouteHandler(
        @Qualifier("platformConnectionEntityTemplate") R2dbcEntityTemplate entityTemplate,
        ObjectMapper objectMapper
    ) {
        this.entityTemplate = entityTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return entityTemplate.select(PlatformConnection.class)
            .matching(
                query(
                    where("user_id").is(request.pathVariable("userId"))
                        .and(where("id").is(request.pathVariable("id")))
                )
            )
            .one()
            .flatMap(dataModel -> {
                ObjectNode responseBody;
                try { responseBody = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(dataModel)); }
                catch (JsonProcessingException exception) { return Mono.error(exception); }

                responseBody.remove("newAccessToken");

                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            })
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;
}
