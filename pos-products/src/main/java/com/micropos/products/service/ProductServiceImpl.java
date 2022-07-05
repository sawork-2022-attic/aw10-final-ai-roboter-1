package com.micropos.products.service;

import com.micropos.products.model.Product;
import com.micropos.products.repository.AmazonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final AmazonRepository productRepository;

    @Autowired
    public ProductServiceImpl( AmazonRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Flux<Product> products() {
        // 默认返回前20个
        System.out.println("get products");
        return Flux.fromIterable(productRepository.findAll(PageRequest.of(1, 20)).getContent());
    }

    @Override
    public Mono<Product> getProduct(Long id) {
        var product = productRepository.findById(id);
        if (product.isEmpty())
            return Mono.empty();
        return Mono.just(product.get());
    }

    @Override
    public Mono<Product> randomProduct() {
        return Mono.empty();
    }
}
