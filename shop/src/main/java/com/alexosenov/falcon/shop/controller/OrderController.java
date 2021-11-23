package com.alexosenov.falcon.shop.controller;

import com.alexosenov.falcon.model.dto.OrderItemDTO;
import com.alexosenov.falcon.shop.service.OrderService;
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
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Async
    @GetMapping
    public CompletableFuture<ResponseEntity> getAllOrders() {
        return orderService.getAllOrders()
                .thenApply(ResponseEntity::ok);
    }

    @Async
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity> createOrder(@RequestBody List<OrderItemDTO> orderItemDTOList) {
        return orderService.createOrder(orderItemDTOList)
                .thenCompose(orderService::attemptToFinishOrder)
                .thenApply(ResponseEntity::ok);
    }
}
