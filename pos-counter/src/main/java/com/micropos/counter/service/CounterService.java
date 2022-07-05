package com.micropos.counter.service;

import reactor.core.publisher.Mono;

public interface CounterService {
    Mono<Boolean> createBill(Integer orderId);
    Mono<Boolean> createBill(Mono<Integer> orderId);

}
