package com.alexosenov.falcon.model.repository;

import com.alexosenov.falcon.model.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
