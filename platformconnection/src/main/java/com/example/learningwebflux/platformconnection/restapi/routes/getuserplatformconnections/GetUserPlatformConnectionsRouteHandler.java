package com.example.learningwebflux.platformconnection.restapi.routes.getuserplatformconnections;

import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GetUserPlatformConnectionsRouteHandler {
    public GetUserPlatformConnectionsRouteHandler(
        @Qualifier("platformConnectionEntityTemplate") R2dbcEntityTemplate entityTemplate,
        ObjectMapper objectMapper
    ) {
        this.entityTemplate = entityTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return entityTemplate.select(PlatformConnection.class)
            .matching(query(buildCriteria(request)))
            .all()
            .flatMap(dataModel -> {
                ObjectNode responseBody;
                try { responseBody = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(dataModel)); }
                catch (JsonProcessingException exception) { return Mono.error(exception); }

                responseBody.remove("newAccessToken");

                return Mono.just(responseBody);
            })
            .collectList()
            .flatMap(data -> {
                var responseBody = JsonNodeFactory.instance.objectNode();
                var dataProperty = responseBody.putArray("data");
                dataProperty.addAll(data);

                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            });
    }

    private Criteria buildCriteria(ServerRequest request) {
        var criteria = where("user_id").is(request.pathVariable("userId"));

        var platformQueryParameter = request.queryParam("platform");
        if (platformQueryParameter.isPresent()) {
            criteria = criteria.and( where("platform").is(platformQueryParameter.get()) );
        }

        return criteria;
    }


    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;


    private static final Logger logger = LoggerFactory.getLogger(GetUserPlatformConnectionsRouteHandler.class);
}
