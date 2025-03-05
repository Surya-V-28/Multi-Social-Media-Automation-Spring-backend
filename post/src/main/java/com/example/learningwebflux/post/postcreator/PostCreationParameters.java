package com.example.learningwebflux.post.postcreator;

import org.springframework.util.MimeType;

import java.net.URI;
import java.net.URL;
import java.util.List;

public record PostCreationParameters(
    String title,
    String caption,
    List<PostMedia> postMedias
) {

    public record PostMedia(
        MimeType mediaType,
        URI url
    ) { }
}
