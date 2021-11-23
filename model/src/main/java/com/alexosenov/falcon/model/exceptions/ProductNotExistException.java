package com.alexosenov.falcon.model.exceptions;

public class ProductNotExistException extends RuntimeException {
    public ProductNotExistException(String msg) {
        super("One of these products doesn't exist: " + msg);
    }
}