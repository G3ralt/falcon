package com.alexosenov.falcon.store.controllers;

import com.alexosenov.falcon.model.dto.ProductDTO;
import com.alexosenov.falcon.store.messaging.MessageProducer;
import com.alexosenov.falcon.store.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin(
        methods = {POST, GET, OPTIONS},
        allowedHeaders = {"origin", "content-type",},
        origins = "*"
)
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping
    public CompletableFuture<ResponseEntity> getAllProducts() {
        return productService.getAllProducts()
                .thenApply(ResponseEntity::ok);
    }

    @Async
    @PostMapping("/stock")
    public CompletableFuture<ResponseEntity> loadMoreProducts(@RequestBody List<ProductDTO> productDTOList) {
        return productService.addStockToProducts(productDTOList)
                .thenApply(products -> {
                    products.forEach(product -> CompletableFuture.runAsync(() -> messageProducer.sendMessage(product)));
                    return products;
                })
                .thenApply(ResponseEntity::ok);
    }

    @Async
    @GetMapping("/stock")
    public CompletableFuture<ResponseEntity> getAllOutOfStockProducts() {
        return productService.getAllOutOfStockProducts()
                .thenApply(ResponseEntity::ok);
    }

}
