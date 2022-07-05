package com.micropos.products.service;

import com.micropos.products.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ProductService {


    Flux<Product> products();

    Mono<Product> getProduct(Long id);

    Mono<Product> randomProduct();
}
