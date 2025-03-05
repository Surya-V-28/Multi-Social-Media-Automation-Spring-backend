package com.example.learningwebflux.auth.jwt;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;

@Component
public class JWTValidator {
    public JWTValidator(WebClient webClient) {
        this.webClient = webClient;
    }

    Mono<Boolean> validateJWT(SignedJWT jwt, URI jwksUrl) {
        return webClient.get()
            .uri(jwksUrl)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(jwkSetString -> {
                JWKSet jwkSet;
                try { jwkSet = JWKSet.parse(jwkSetString); }
                catch (ParseException exception) { return Mono.error(exception);  }

                RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(jwt.getHeader().getKeyID());

                JWSVerifier verifier;
                try { verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey()); }
                catch (Exception exception) { return Mono.just(false); }

                try {
                    if (!jwt.verify(verifier)) {
                        return Mono.just(false);
                    }
                }
                catch (Exception exception) { return Mono.just(false); }

                final JWTClaimsSet claims;
                try { claims = jwt.getJWTClaimsSet(); }
                catch (ParseException exception) { return Mono.just(false); }

                if (claims.getExpirationTime() != null && claims.getExpirationTime().before(new Date())) {
                    return Mono.just(false);
                }

                return Mono.just(true);
            });
    }


    private final WebClient webClient;
}
