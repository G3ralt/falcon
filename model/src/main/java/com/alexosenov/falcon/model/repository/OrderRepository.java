package com.alexosenov.falcon.model.repository;

import com.alexosenov.falcon.model.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStatus(Order.Status status);

}
