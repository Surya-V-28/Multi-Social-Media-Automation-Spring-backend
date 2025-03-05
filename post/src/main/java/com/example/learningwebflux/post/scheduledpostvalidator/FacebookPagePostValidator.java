package com.example.learningwebflux.post.scheduledpostvalidator;

import org.springframework.util.MimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FacebookPagePostValidator implements PostTargetValidator {
    @Override
    public List<String> validate(ValidatingPost validatingPost) {
        var validationErrors = new ArrayList<String>();

        if (validatingPost.caption().length() > 63206) {
            validationErrors.add("Caption length needs to be below 63206");
        }

        var areImageMimeTypesValid = validatingPost.media()
            .stream()
            .filter(media -> media.details() instanceof ValidatingPost.ImageMediaDetails)
            .allMatch(media -> imageFormats.contains(media.mimeType()));

        if (!areImageMimeTypesValid) {
            validationErrors.add("The only allowed image formats are jpg, png, and gif");
        }

        var areVideoMimeTypesValid = validatingPost.media()
            .stream()
            .filter(media -> media.details() instanceof ValidatingPost.VideoMediaDetails)
            .allMatch(media -> videoFormats.contains(media.mimeType()));

        if (!areVideoMimeTypesValid) {
            validationErrors.add("The only allowed image formats are mp4 and mov");
        }

        return validationErrors;
    }

    private final Set<MimeType> imageFormats = Set.of(MimeType.valueOf("image/jpeg"), MimeType.valueOf("image/png"), MimeType.valueOf("image/gif"));
    private final Set<MimeType> videoFormats = Set.of(MimeType.valueOf("video/mp4"), MimeType.valueOf("video/mov"));
}
