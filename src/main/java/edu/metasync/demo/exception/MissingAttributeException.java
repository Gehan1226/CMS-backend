package edu.metasync.demo.exception;

public class MissingAttributeException extends RuntimeException{

    public MissingAttributeException(String message) {
        super(message);
    }
}
