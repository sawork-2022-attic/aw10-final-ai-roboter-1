package com.micropos.cart.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropos.api.ItemApi;
import com.micropos.cart.mapper.CartMapper;
import com.micropos.cart.mapper.OrderMapper;
import com.micropos.cart.service.PosService;
import com.micropos.dto.*;
import com.micropos.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@RestController
@RequestMapping("api")
@CrossOrigin
public class CartController implements ItemApi {

    private final PosService posService;

    private CartMapper cartMapper;

    private OrderMapper orderMapper;


    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    public CartController(PosService service) {
        this.posService = service;
    }

    private Mono<Cart> convertToCart(Mono<WebSession> sessionMono) {
        return sessionMono.publishOn(Schedulers.boundedElastic()).mapNotNull(s -> {
            Cart cart = s.getAttribute("cart");
            if (cart == null){
                cart = new Cart();
                s.getAttributes().put("cart", cart);
                s.save().block();
            }
            return cart;
        });
    }

    @Override
    public Mono<ResponseEntity<CartDto>> showCartItems( ServerWebExchange exchange) {
        var sessionMono = exchange.getSession();
        return convertToCart(sessionMono).map(cartMapper::toCartDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ItemDto>> addItem(Integer productId, Mono<ItemFiledDto> itemFiledDto, ServerWebExchange exchange) {
        var sessionMono = exchange.getSession();
        var quantity = itemFiledDto.map(ItemFiledDto::getQuantity);
        var cartMono = convertToCart(sessionMono);
        return quantity.publishOn(Schedulers.boundedElastic()).map(v -> {
            var result = posService.add(productId, v, cartMono.block(), sessionMono.block());
            sessionMono.map(WebSession::save).block();
            return result;
        }).map(cartMapper::toItemDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ItemDto>> deleteItem(Integer productId, ServerWebExchange exchange) {
        var sessionMono = exchange.getSession();
        var cartMono = convertToCart(sessionMono);
        return cartMono.map(cart -> {
            var result = posService.removeItem(productId,cart, sessionMono.block());
            sessionMono.map(WebSession::save).block();
            return result;
        }).map(cartMapper::toItemDto).map(ResponseEntity::ok);

    }

    @Override
    public Mono<ResponseEntity<ItemDto>> updateItem(Integer productId, Mono<ItemFiledDto> itemFiledDto,
                                                    ServerWebExchange exchange) {
        var sessionMono = exchange.getSession();
        var cartMono = convertToCart(sessionMono);
        return itemFiledDto.map(ItemFiledDto::getQuantity).publishOn(Schedulers.boundedElastic()).map(s ->  {
            var result = posService.modifyItem(productId, s, cartMono.block(), sessionMono.block());
            sessionMono.map(WebSession::save).block();
            return result;
        }).map(cartMapper::toItemDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<MessageDto>> deleteAllItems(ServerWebExchange exchange) {
        return exchange.getSession().map(session -> {
            session.getAttributes().put("cart", new Cart());
            session.save();
            MessageDto messageDto = new MessageDto();
            messageDto.setSuccess(true);
            messageDto.setMessage("delete successes");
            return messageDto;
        }).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> convertCartToOrder(Mono<CartDto> cartDto, ServerWebExchange exchange) {
        deleteAllItems(exchange);
        return cartDto.map(cartDto1 -> {System.out.println(cartDto1); return cartDto1;}).map(cartMapper::toCart).map(posService::createOrder).
                map(ResponseEntity::ok);
    }


}
