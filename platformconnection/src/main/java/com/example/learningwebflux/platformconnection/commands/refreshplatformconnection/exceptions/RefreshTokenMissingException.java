package com.example.learningwebflux.platformconnection.commands.refreshplatformconnection.exceptions;

public class RefreshTokenMissingException extends Exception {
    public RefreshTokenMissingException(String platformConnectionId) {
        super(String.format("Refresh token for Platform Connection %s could not be found", platformConnectionId));
    }
}
