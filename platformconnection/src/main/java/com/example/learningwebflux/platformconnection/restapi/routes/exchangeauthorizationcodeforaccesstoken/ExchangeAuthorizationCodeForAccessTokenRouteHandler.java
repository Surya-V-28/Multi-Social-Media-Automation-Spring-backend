package com.example.learningwebflux.platformconnection.restapi.routes.exchangeauthorizationcodeforaccesstoken;

import com.example.learningwebflux.platformconnection.Platform;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExchangeAuthorizationCodeForAccessTokenRouteHandler {
    public ExchangeAuthorizationCodeForAccessTokenRouteHandler(
        WebClient webClient,

        @Value("${facebook.appId}") String facebookAppId,
        @Value("${facebook.appSecret}") String facebookAppSecret,

        @Value("${instagram.appId}") String instagramAppId,
        @Value("${instagram.appSecret}") String instagramAppSecret
    ) {
        this.webClient = webClient;

        this.facebookAppId = facebookAppId;
        this.facebookAppSecret = facebookAppSecret;

        this.instagramAppId = instagramAppId;
        this.instagramAppSecret = instagramAppSecret;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(RequestBody.class)
            .flatMap(requestBody -> {
                if (requestBody.platform() == Platform.facebook) {
                    return handleFacebook(requestBody);
                }
                else if (requestBody.platform() == Platform.instagram) {
                    return handleInstagram(requestBody);
                }
                else {
                    var exception = new Exception(String.format("Platform %s is not handled", requestBody.platform()));
                    return Mono.error(exception);
                }
            });
    }

    private Mono<ServerResponse> handleFacebook(RequestBody requestBody) {
        var url = new DefaultUriBuilderFactory().builder()
            .scheme("https")
            .host("graph.facebook.com")
            .path("/v21.0/oauth/access_token")
            .queryParam("client_id", facebookAppId)
            .queryParam("client_secret", facebookAppSecret)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("code", requestBody.authorizationCode())
            .build();

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMap(responseBody -> {
                var longLivedAccessTokenUrl = new DefaultUriBuilderFactory().builder()
                    .scheme("https")
                    .host("graph.facebook.com")
                    .path("/v21.0/oauth/access_token")
                    .queryParam("client_id", facebookAppId)
                    .queryParam("client_secret", facebookAppSecret)
                    .queryParam("grant_type", "fb_exchange_token")
                    .queryParam("fb_exchange_token", responseBody.get("access_token").asText())
                    .build();

                return webClient.get()
                    .uri(longLivedAccessTokenUrl)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .flatMap(longLivedAccessTokenResponseBody -> {
                        var accessToken = longLivedAccessTokenResponseBody.get("access_token").asText();

                        var expiresIn = (longLivedAccessTokenResponseBody.has("expires_in")) ?
                            longLivedAccessTokenResponseBody.get("expires_in").asLong() :
                            -1;
                        var expiresAt = (expiresIn == -1) ?
                            OffsetDateTime.now().plusYears(20) :
                            OffsetDateTime.now().plusSeconds(expiresIn);

                        return sendResponse(accessToken, accessToken, expiresAt);
                    });
            });
    }

    private Mono<ServerResponse> handleInstagram(RequestBody requestBody) {
        var url = new DefaultUriBuilderFactory().builder()
            .scheme("https")
            .host("api.instagram.com")
            .path("/oauth/access_token")
            .build();

        var requestBodyForm = new LinkedMultiValueMap<String, String>();
        requestBodyForm.add("client_id", instagramAppId);
        requestBodyForm.add("client_secret", instagramAppSecret);
        requestBodyForm.add("grant_type", "authorization_code");
        requestBodyForm.add("redirect_uri", redirectUri);
        requestBodyForm.add("code", requestBody.authorizationCode());

        return webClient.post()
            .uri(url)
            .body(BodyInserters.fromFormData(requestBodyForm))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMap(responseBody -> Mono.just(responseBody.get("access_token").asText()))
            .flatMap(accessToken -> {
                var exchangeTokenUrl = new DefaultUriBuilderFactory().builder()
                    .scheme("https")
                    .host("graph.instagram.com")
                    .path("/access_token")
                    .queryParam("client_secret", instagramAppSecret)
                    .queryParam("grant_type", "ig_exchange_token")
                    .queryParam("access_token", accessToken)
                    .build();

                return webClient.get()
                    .uri(exchangeTokenUrl)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .flatMap(exchangeTokenResponseBody -> {
                        var longLivedAccessToken = exchangeTokenResponseBody.get("access_token").asText();

                        var expiresIn = exchangeTokenResponseBody.get("expires_in").asLong(-1);
                        var expiresAt = (expiresIn == -1) ?
                            OffsetDateTime.now().plusYears(20) :
                            OffsetDateTime.now().plusSeconds(expiresIn);

                        return sendResponse(longLivedAccessToken, longLivedAccessToken, expiresAt);
                    });

            });
    }

    private Mono<ServerResponse> sendResponse(String accessToken, String refreshToken, OffsetDateTime expiresAt) {
        return ServerResponse.ok()
            .body(BodyInserters.fromValue(
                new ResponseBody(accessToken, refreshToken, expiresAt.format(DateTimeFormatter.ISO_DATE_TIME)))
            );
    }



    private final WebClient webClient;

    private final String facebookAppId;
    private final String facebookAppSecret;

    private final String instagramAppId;
    private final String instagramAppSecret;


    private static final String redirectUri = "https://rrupzduwseavjd2nfl3caf33va0shwhb.lambda-url.ap-south-1.on.aws/platform-connection-oauth-callback";
}

record RequestBody(Platform platform, String authorizationCode) {}

record ResponseBody(String accessToken, String refreshToken, String expiresAt) {}
