package com.example.learningwebflux.post.restapi.routes.getscheduledposts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import com.example.learningwebflux.post.r2dbc.models.ScheduledPostDataModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.OffsetDateTime;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class GetUsersScheduledPostsRouteHandler {
    public GetUsersScheduledPostsRouteHandler(
        @Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate,
        ObjectMapper objectMapper,
        S3Presigner s3Presigner,
        @Value("${aws.s3.bucket}") String bucketName
    ) {
        this.entityTemplate = entityTemplate;
        this.objectMapper = objectMapper;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }


    public Mono<ServerResponse> handle(ServerRequest request) {
        var criteria = buildCriteria(request);

        return entityTemplate.select(ScheduledPostDataModel.class)
            .matching(query(criteria))
            .all()
            .flatMap(this::createScheduledPostResponseBody)
            .collectList()
            .flatMap(scheduledPostsResponseBody -> {
                var responseBody = JsonNodeFactory.instance.objectNode();
                var dataProperty = responseBody.putArray("data");
                dataProperty.addAll(scheduledPostsResponseBody);

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

    private Criteria buildCriteria(ServerRequest request) {
        Criteria criteria = where("user_id").is(request.pathVariable("userId"));

        var fromTimeQueryParameter = request.queryParam("fromTime");
        if (fromTimeQueryParameter.isPresent()) {
            var fromTime = OffsetDateTime.parse(fromTimeQueryParameter.get());
            criteria = extendCriteria(criteria, where("scheduled_time").greaterThanOrEquals(fromTime));
        }

        var toTimeQueryParameter = request.queryParam("toTime");
        if (toTimeQueryParameter.isPresent()) {
            var toTime = OffsetDateTime.parse(toTimeQueryParameter.get());
            criteria = extendCriteria(criteria, where("scheduled_time").lessThan(toTime));
        }

        return criteria;
    }

    private Criteria extendCriteria(Criteria criteria, Criteria extendWith) {
        if (criteria == null) return extendWith;
        else return criteria.and(extendWith);
    }




    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;
    private final S3Presigner s3Presigner;
    private final String bucketName;
}