package com.micropos.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropos.model.Cart;
import com.micropos.model.Order;
import com.micropos.model.OrderItem;
import com.micropos.model.Product;
import com.micropos.order.repository.OrderItemRepository;
import com.micropos.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    private OrderRepository orderRepository;

//    private final String COUNTER_URL = "http://POS-DELIVERY/counter/";

    private static final String QUEUE_NAME = "OrderQueue";

    @LoadBalanced
    private RestTemplate restTemplate;

    @LoadBalanced
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrderItemRepository orderItemRepository;

    @Autowired
    public void setOrderItemRepository(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public Mono<Optional<Order>> createOrder(Cart cart) {
        if (cart.getItems().isEmpty()) {
            return Mono.just(Optional.empty());
        }

        Mono<Cart> cartMono = Mono.just(cart);
        return this.createOrder(cartMono);
    }

    @Override
    public Mono<Optional<Order>> createOrder(Mono<Cart> cartMono) {
        var orderMono = Mono.just(new Order());
        return orderMono.publishOn(Schedulers.boundedElastic()).map(order -> {
            order.setOrderItems(new ArrayList<>());
            cartMono.map(Cart::getItems).publishOn(Schedulers.boundedElastic()).mapNotNull(items -> {
                System.out.println(items);
                for(var item: items) {
                    var orderItem = new OrderItem();
                    orderItem.product(new Product());
                    orderItem.product().id(item.product().id());
                    orderItem.quantity(item.quantity());
                    order.addOrderItem(orderItem);
                }
                orderItemRepository.saveAll(order.getOrderItems());
                return null;
            }).block();
            return order;
        }).publishOn(Schedulers.boundedElastic()).map(order -> orderRepository.save(order)).map(Optional::of);
    }

    @Override
    public Mono<Optional<Order>> delivery(Integer orderId)  {
        Mono<Integer> orderIdMono = Mono.just(orderId);
        return orderIdMono.map(orderRepository::findById).map(order -> {
            if (order.isEmpty())
                return Optional.empty();
                var orderInfo = order.get().getOrderItems().stream().map(s ->
                        s.product().name() + " " + s.product().price() + " quantity: " + s.quantity()
                ).collect(Collectors.joining(" "));
                rabbitTemplate.convertAndSend(QUEUE_NAME, "", orderInfo);
                return order;
        });
    }

    @Override
    public Mono<Optional<Order>> getOrder(Integer orderId) {
        return Mono.just(orderId).map(orderRepository::findById);
    }

    public String test() {
        return String.valueOf(orderRepository.findAll().get(0).getOrderItems().get(1).quantity());
    }
}
