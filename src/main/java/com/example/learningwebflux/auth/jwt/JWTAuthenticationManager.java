package com.example.learningwebflux.auth.jwt;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {
    public JWTAuthenticationManager(JWTValidator jwtValidator, @Value("${aws.cognito.jwkSetUrl}") String jwkSetURL) {
        this.jwtValidator = jwtValidator;
        this.jwkSetURL = URI.create(jwkSetURL);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var jwtAuthentication = (JWTAuthentication) authentication;

        var jwt = (SignedJWT) jwtAuthentication.getCredentials();

        return jwtValidator.validateJWT(jwt, jwkSetURL)
            .onErrorReturn(false)
            .map(
                isValid -> {
                    authentication.setAuthenticated(isValid);
                    return authentication;
                }
            );
    }

    private final JWTValidator jwtValidator;
    private final URI jwkSetURL;
}
