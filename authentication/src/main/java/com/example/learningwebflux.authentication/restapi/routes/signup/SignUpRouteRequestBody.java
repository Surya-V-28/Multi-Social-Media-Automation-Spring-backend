package com.example.learningwebflux.authentication.restapi.routes.signup;

public record SignUpRouteRequestBody(
    String username,
    String email,
    String password
) {}
