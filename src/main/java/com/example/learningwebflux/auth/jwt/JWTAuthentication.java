package com.example.learningwebflux.auth.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.text.ParseException;

public class JWTAuthentication extends AbstractAuthenticationToken {
    JWTAuthentication(String jwtString) throws ParseException {
        super(AuthorityUtils.NO_AUTHORITIES);

        this.jwtString = jwtString;
        this.jwt = SignedJWT.parse(jwtString);
        this.claims = jwt.getJWTClaimsSet();
    }

    @Override
    public String getName() {
        return claims.getSubject();
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return claims.getSubject();
    }

    public String getJwtString() { return jwtString; }


    private final String jwtString;
    private final SignedJWT jwt;
    private final JWTClaimsSet claims;
}
