package com.micropos.order.rest;

import com.micropos.api.OrderApi;
import com.micropos.dto.CartDto;
import com.micropos.dto.OrderDto;
import com.micropos.order.mapper.CartMapper;
import com.micropos.order.mapper.OrderMapper;
import com.micropos.model.Cart;
import com.micropos.order.repository.OrderItemRepository;
import com.micropos.order.service.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("api")
public class OrderController implements OrderApi {

    private OrderService orderService;

    private OrderMapper orderMapper;

    private CartMapper cartMapper;



    @Autowired
    public void setOrderService(OrderService orderService) {this.orderService = orderService;}

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> getOrder(Long orderId, ServerWebExchange exchange) {
        return orderService.getOrder(Math.toIntExact(orderId)).mapNotNull(order -> {
            if (order.isEmpty())
                return null;
            return orderMapper.toOrderDto(order.get());
        }).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> createOrder(@NotNull Mono<CartDto> cartDto, ServerWebExchange exchange) {
        var orderMono =  orderService.createOrder(cartDto.map(cartMapper::toCart));

        return orderMono.publishOn(Schedulers.boundedElastic()).map(order -> {
            if (order.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            orderService.delivery(order.get().id()).block();
            return ResponseEntity.ok(orderMapper.toOrderDto(order.get()));
        });
    }

    @RequestMapping(value = "/")
    public String test() {
        return orderService.test();
    }
}
