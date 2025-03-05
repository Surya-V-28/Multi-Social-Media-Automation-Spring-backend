package com.example.learningwebflux.post.commands;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.scheduledpostvalidator.ScheduledPostValidator;
import com.example.learningwebflux.post.scheduledpostvalidator.ValidatingPost;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ValidatePostCommandHandler {
    public Mono<Map<PostTargetType, List<String>>> validate(
        List<PostTargetType> postTargetTypes,
        ValidatingPost validatingPost
    ) {
        return Mono.just(scheduledPostValidator.validate(postTargetTypes, validatingPost));
    }



    private final ScheduledPostValidator scheduledPostValidator;
}
