package com.example.learningwebflux.common.integrationevents.medialibrarymediaadded;

public record ImageMediaDetails(
    int width,
    int height
) implements MediaDetails { }
