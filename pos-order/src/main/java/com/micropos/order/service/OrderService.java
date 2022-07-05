package com.micropos.order.service;

import com.micropos.model.Cart;
import com.micropos.model.Order;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface OrderService {

   Mono<Optional<Order>> createOrder(Cart cart);

   Mono<Optional<Order>> createOrder(Mono<Cart> cart);


   Mono<Optional<Order>> delivery(Integer orderId);

   Mono<Optional<Order>> getOrder(Integer orderId);

   //todo
   String test();
}
