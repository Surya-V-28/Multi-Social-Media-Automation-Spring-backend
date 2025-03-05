package com.example.learningwebflux.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.learningwebflux.auth.jwt.JWTAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfiguration {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity security,
        JWTAuthenticationConverter authenticationConverter,
        ReactiveAuthenticationManager authenticationManager
    ) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setServerAuthenticationConverter(authenticationConverter);

        return security
            .authorizeExchange(
                specification -> {
                    specification
//                        .pathMatchers("/**").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/platform-connection/facebook/**").permitAll()
                        .pathMatchers("/api/platform-connection/exchange-authorization-code-for-access-token").permitAll()
                        .pathMatchers("/api/platform-connection/refresh-all").permitAll()
                        .pathMatchers("/api/post/posts/validate").permitAll()
                        .pathMatchers("/api/post-analytics/testing/**").permitAll()
                        .anyExchange().authenticated();
                }
            )
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic(specification -> specification.disable())
            .formLogin(specification -> specification.disable())
            .csrf(specification -> specification.disable())
            .cors(specification -> specification.disable())
            .build();
    }
}
