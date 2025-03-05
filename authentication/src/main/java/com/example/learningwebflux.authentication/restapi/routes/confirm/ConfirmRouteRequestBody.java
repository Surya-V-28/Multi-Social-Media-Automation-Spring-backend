package com.example.learningwebflux.authentication.restapi.routes.confirm;

public record ConfirmRouteRequestBody(String email, String code) {}