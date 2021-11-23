package com.alexosenov.falcon.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    private @NonNull Long productId;
    private @NonNull int quantity;

}
