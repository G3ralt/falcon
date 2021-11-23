package com.alexosenov.falcon.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash
public class Product {

    @Id
    private Long productId;
    private String name;
    private int quantity;

    public synchronized void addQuantity(int amountToAdd) {
        this.quantity += amountToAdd;
    }

}
