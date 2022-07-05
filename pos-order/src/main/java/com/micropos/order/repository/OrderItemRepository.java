package com.micropos.order.repository;

import com.micropos.model.Order;
import com.micropos.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
}
