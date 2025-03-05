package com.example.learningwebflux.platformconnection.accesstokenrefresher;

import com.example.learningwebflux.platformconnection.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccessTokenRefresherRegistry {
    public AccessTokenRefresherRegistry(
        FacebookAccessTokenRefresher facebookRefresher,
        InstagramAccessTokenRefresher instagramRefresher
    ) {
        items = Map.of(
            Platform.facebook, facebookRefresher,
            Platform.instagram, instagramRefresher
        );
    }

    public AccessTokenRefresher getItem(Platform platform) {
        return items.get(platform);
    }


    private final Map<Platform, AccessTokenRefresher> items;
}