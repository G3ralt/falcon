package com.alexosenov.falcon.shop.messaging;

import com.alexosenov.falcon.model.entity.Order;
import com.alexosenov.falcon.model.entity.Product;
import com.alexosenov.falcon.shop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(ProductConsumer.class);

    @Autowired
    private OrderService orderService;

    public void handleMessage(Product product) {

        LOG.info("Product restock: {}", product);

        orderService.attemptToFinishOrdersHavingProduct(product)
                .thenAccept(orders -> LOG.info("Didn't finish: {}", orders.stream().filter(order -> order.getStatus().equals(Order.Status.ACCEPTED)).collect(Collectors.toList())));
    }

}
