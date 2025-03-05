package com.example.learningwebflux.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;

@Configuration
public class CognitoConfiguration {
    @Bean
    public CognitoIdentityProviderAsyncClient cognitoClient() {
        return CognitoIdentityProviderAsyncClient.builder()
            .region(Region.AP_SOUTH_1)
            .build();
    }
}
