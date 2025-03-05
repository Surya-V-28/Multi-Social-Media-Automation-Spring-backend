package com.example.learningwebflux.post.scheduledpostvalidator;

import com.example.learningwebflux.post.PostTargetType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduledPostValidator {
    public ScheduledPostValidator() {
        validators = Map.of(
            PostTargetType.facebookPage, new FacebookPagePostValidator(),
            PostTargetType.instagramFeed, new InstagramFeedPostValidator()
        );
    }

    public Map<PostTargetType, List<String>> validate(List<PostTargetType> targets, ValidatingPost validatingPost) {
        var validationErrors = new HashMap<PostTargetType, List<String>>();

        for (PostTargetType target : targets) {
            var validator = validators.get(target);
            validationErrors.put(target, validator.validate(validatingPost));
        }

        return validationErrors;
    }


    private final Map<PostTargetType, PostTargetValidator> validators;
}


