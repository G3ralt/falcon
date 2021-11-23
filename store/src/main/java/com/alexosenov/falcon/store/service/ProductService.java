package com.alexosenov.falcon.store.service;

import com.alexosenov.falcon.model.dto.ProductDTO;
import com.alexosenov.falcon.model.entity.Order;
import com.alexosenov.falcon.model.entity.OrderItem;
import com.alexosenov.falcon.model.entity.Product;
import com.alexosenov.falcon.model.exceptions.ProductNotExistException;
import com.alexosenov.falcon.model.repository.OrderRepository;
import com.alexosenov.falcon.model.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public CompletableFuture<List<Product>> getAllProducts() {
        return CompletableFuture.supplyAsync(() -> IterableConverter.toList(productRepository.findAll()));
    }

    public CompletableFuture<List<Product>> addStockToProducts(List<ProductDTO> productDTOList) {
        return CompletableFuture.supplyAsync(() -> IterableConverter.toList(productRepository.findAllById(productDTOList.stream().map(ProductDTO::getProductId).collect(Collectors.toList()))))
                .thenApply(productsFromDb -> {
                    if (productsFromDb.size() != productDTOList.size()) {
                        throw new ProductNotExistException(productDTOList.toString());
                    }
                    Map<Long, ProductDTO> prodMap = productDTOList.stream().collect(Collectors.toMap(ProductDTO::getProductId, Function.identity()));
                    return productsFromDb.stream().peek(product -> {
                        int additionalQuantity = prodMap.get(product.getProductId()).getQuantity();
                        product.addQuantity(additionalQuantity);
                    }).collect(Collectors.toList());
                })
                .thenApply(productRepository::saveAll)
                .thenApply(IterableConverter::toList);
    }

    public CompletableFuture<Map<String, Integer>> getAllOutOfStockProducts() {
        return CompletableFuture.supplyAsync(() -> orderRepository.findAllByStatus(Order.Status.ACCEPTED))
                .thenApply(orders -> orders.stream()
                        .map(Order::getItemList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .thenApply(orderItems -> {
                    Map<Long, Integer> neededQuantity = orderItems.stream()
                            .collect(Collectors.toMap(orderItem -> orderItem.getProduct().getProductId(), OrderItem::getQuantity, Integer::sum));
                    Set<Product> products = orderItems.stream()
                            .map(OrderItem::getProduct)
                            .collect(Collectors.toSet());
                    return products.stream()
                            .filter(product -> (product.getQuantity() - neededQuantity.get(product.getProductId()) < 0))
                            .collect(Collectors.toMap(Product::getName, product -> Math.abs((product.getQuantity() - neededQuantity.get(product.getProductId())))));
                });
    }

}
