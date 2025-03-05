package com.example.learningwebflux.post.scheduledpostvalidator;

import java.util.List;

public interface PostTargetValidator {
    List<String> validate(ValidatingPost validatingPost);
}
