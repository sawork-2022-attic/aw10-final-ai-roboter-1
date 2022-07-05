package com.micropos.counter.service;

import com.micropos.counter.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
public class CounterServiceImp implements CounterService{

    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<Boolean> createBill(Integer orderId) {
        return createBill(Mono.just(orderId));
    }

    @Override
    public Mono<Boolean> createBill(Mono<Integer> orderIdMono) {
        return orderIdMono.publishOn(Schedulers.boundedElastic()).map(orderId -> {
            var orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty())
                return false;
            var order = orderOpt.get();
            order.paid(false);
            orderRepository.save(order);
            return true;
        });
    }
}
