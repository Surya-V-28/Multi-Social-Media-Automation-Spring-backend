package com.example.learningwebflux.authentication.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CognitoUtils {
    public static String calculateSecretHash(String username, String clientId, String clientSecret) throws InvalidKeyException {
        final String message = username + clientId;
        final Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        }
        catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            return "";
        }

        mac.init(new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256"));

        byte[] hashBytes = mac.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
