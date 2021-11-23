package com.alexosenov.falcon.shop.controller;

import com.alexosenov.falcon.model.exceptions.ProductNotExistException;
import com.alexosenov.falcon.shop.ShopApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ShopApplication.class);

    @ExceptionHandler(value = Exception.class)
    public final ResponseEntity<String> handleException(Exception exception) {
        LOG.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(value = ProductNotExistException.class)
    public final ResponseEntity<String> handleProductNotExistException(ProductNotExistException exception) {
        LOG.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
