package com.alexosenov.falcon.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@RedisHash
public class Order {

    public enum Status {
        ACCEPTED,COMPLETED
    }

    @Id
    private Long orderId;

    private List<OrderItem> itemList;

    @Indexed
    private Status status;



}
