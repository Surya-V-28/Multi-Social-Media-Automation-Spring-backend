package com.example.learningwebflux.auth.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
            .filter(headerValue -> headerValue.substring(0, "BEARER ".length()).equalsIgnoreCase("bearer "))
            .map(header -> header.substring("BEARER ".length()))
            .flatMap(jwtString -> {
                try { return Mono.just(new JWTAuthentication(jwtString)); }
                catch (Exception exception) { return Mono.error(exception); }
            });
    }
}
