package com.alexosenov.falcon.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@Builder
@RedisHash
public class OrderItem {

    @Id
    private Long orderItemId;
    @Reference
    private Product product;
    private int quantity;

}
