package com.alexosenov.falcon.shop.service;

import com.alexosenov.falcon.model.dto.OrderItemDTO;
import com.alexosenov.falcon.model.entity.Order;
import com.alexosenov.falcon.model.entity.OrderItem;
import com.alexosenov.falcon.model.entity.Product;
import com.alexosenov.falcon.model.exceptions.ProductNotExistException;
import com.alexosenov.falcon.model.repository.OrderItemRepository;
import com.alexosenov.falcon.model.repository.OrderRepository;
import com.alexosenov.falcon.model.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final static Logger LOG = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public CompletableFuture<Order> createOrder(List<OrderItemDTO> orderItemDTOList) {
        return CompletableFuture.supplyAsync(() -> {
                    List<Long> productIds = orderItemDTOList.stream().map(OrderItemDTO::getProductId).collect(Collectors.toList());
                    List<Product> products = IterableConverter.toList(productRepository.findAllById(productIds));
                    if (orderItemDTOList.size() != products.size()) {
                        throw new ProductNotExistException(orderItemDTOList.toString());
                    }
                    return products.stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));
                })
                .thenApply(productMap -> orderItemDTOList.stream()
                        .map(orderItemDTO -> OrderItem.builder().product(productMap.get(orderItemDTO.getProductId())).quantity(orderItemDTO.getQuantity()).build())
                        .collect(Collectors.toList())
                )
                .thenApply(orderItems -> {
                    Order createdOrder = Order.builder().status(Order.Status.ACCEPTED).build();
                    List<OrderItem> savedItems = IterableConverter.toList(orderItemRepository.saveAll(orderItems));
                    createdOrder.setItemList(savedItems);
                    return orderRepository.save(createdOrder);
                });
    }

    public CompletableFuture<List<Order>> getAllOrders() {
        return CompletableFuture.supplyAsync(() -> IterableConverter.toList(orderRepository.findAll()));
    }

    public CompletableFuture<Order> attemptToFinishOrder(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            boolean hasProductOutOfStock = order.getItemList().stream()
                    .anyMatch(orderItem -> orderItem.getQuantity() > orderItem.getProduct().getQuantity());
            if (hasProductOutOfStock) {
                LOG.info("Order has products out of stock: {}", order);
                return order;
            }
            List<Product> updatedProducts = order.getItemList().stream()
                    .map(orderItem -> {
                        Product product = orderItem.getProduct();
                        product.addQuantity(Math.negateExact(orderItem.getQuantity()));
                        return product;
                    })
                    .collect(Collectors.toList());
            productRepository.saveAll(updatedProducts);
            order.setStatus(Order.Status.COMPLETED);
            LOG.info("Finished order: {}", order);
            return orderRepository.save(order);
        });
    }

    public CompletableFuture<List<Order>> attemptToFinishOrdersHavingProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> {
                    List<Order> acceptedOrders = orderRepository.findAllByStatus(Order.Status.ACCEPTED);
                    return acceptedOrders.stream()
                            .filter(order -> order.getItemList().stream().anyMatch(orderItem -> orderItem.getProduct().getProductId().equals(product.getProductId())))
                            .collect(Collectors.toList());
                })
                .thenApply(orders -> orders.stream().map(this::attemptToFinishOrder).collect(Collectors.toList()))
                .thenApply(completableFutures -> {
                    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
                    return completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
                });
    }

}
