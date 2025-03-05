package com.example.learningwebflux.platformconnection.commands.common.exceptions;

public class PlatformConnectionAlreadyExistsException extends Exception {
    public PlatformConnectionAlreadyExistsException(String id) {
        super(String.format("Platform Connection already exists %s", id));
    }
}
