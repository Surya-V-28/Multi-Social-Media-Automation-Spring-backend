package com.example.learningwebflux.platformconnection.accesstokenvaliditychecker;

import com.example.learningwebflux.platformconnection.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccessTokenValidityCheckerRegistry {
    public AccessTokenValidityCheckerRegistry(
        FacebookAccessTokenValidityChecker facebookItem,
        InstagramAccessTokenValidityChecker instagramItem
    ) {
        this.records = Map.of(
            Platform.facebook, facebookItem,
            Platform.instagram, instagramItem
        );
    }

    public AccessTokenValidityChecker get(Platform platform) { return records.get(platform); }



    private final Map<Platform, AccessTokenValidityChecker> records;
}
