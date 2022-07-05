package com.micropos.cart.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropos.cart.mapper.CartMapper;
import com.micropos.cart.repository.AmazonRepository;
import com.micropos.dto.CartDto;
import com.micropos.dto.OrderDto;
import com.micropos.model.Cart;
import com.micropos.model.Item;
import com.micropos.model.Order;
import com.micropos.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.WebSession;

import java.io.Serializable;

@Component
public class PosServiceImp implements PosService, Serializable {

    private AmazonRepository amazonRepository;


    private RestTemplate restTemplate;

    private String ORDER_SERVER_URL = "http://ORDER-SERVICE/api";

    private CartMapper cartMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setOrderMapper(CartMapper orderMapper) {
        this.cartMapper = orderMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setDb(AmazonRepository amazonRepository) {
        this.amazonRepository = amazonRepository;
    }

    @Override
    public Item getItem(Integer productId, Cart cart) {
        return cart.getItems().stream()
                            .filter(item -> item.product().id().equals(productId))
                            .findFirst().orElse(null);
    }

    @Override
    public void checkout(Cart cart) {

    }

    @Override
    public Item add(Product product, int amount, Cart cart, WebSession webSession) {
        return add(product.id(), amount, cart, webSession);
    }

    @Override
    public Item add(Integer productId, int amount, Cart cart, WebSession webSession) {
        var  product = amazonRepository.findById(productId);
        if (product.isEmpty()) return null;
        var item = new Item();
        item.product(product.get());
        item.quantity(amount);
        var result = cart.addItem(item);
        webSession.getAttributes().put("cart",cart);
        webSession.save().block();
        return result;
    }

    @Override
    public Item removeItem(Integer productId, Cart cart, WebSession webSession) {
        var items = cart.getItems();
        var target = items.stream().filter(item -> item.product().id().equals(productId))
                .findFirst().orElse(null);
        if (target == null)
            return null;

        items.remove(target);
        webSession.getAttributes().put("cart", cart);
        webSession.save().block();
        return target;
    }

    @Override
    public Item modifyItem(Integer productId, int quantity, Cart cart, WebSession webSession) {
        if (cart.getItem(productId) != null) {
            var result = cart.modifyItem(productId, quantity, webSession);
            webSession.getAttributes().put("cart", cart);
            webSession.save().block();
            return result;
        } else {
            return add(productId, quantity, cart, webSession);
        }
    }

    @Override
    public OrderDto createOrder(Cart cart) {
        CartDto cartDto = cartMapper.toCartDto(cart);
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = null;
        try {
            request = new HttpEntity<>(mapper.writeValueAsString(cartDto), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return restTemplate.postForObject(ORDER_SERVER_URL + "/order", request, OrderDto.class);
    }
}
