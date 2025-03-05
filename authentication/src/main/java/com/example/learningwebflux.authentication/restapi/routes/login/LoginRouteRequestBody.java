package com.example.learningwebflux.authentication.restapi.routes.login;


public record LoginRouteRequestBody(
    String email,
    String password
) {}