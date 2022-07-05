package com.micropos.delivery.rest;

import com.micropos.api.DeliveryApi;
import com.micropos.delivery.service.DeliveryService;
import com.micropos.dto.DeliveryDto;
import com.micropos.dto.OrderDto;
import com.micropos.delivery.mapper.DeliveryMapper;
import com.micropos.delivery.mapper.OrderMapper;
import com.micropos.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api")
public class DeliveryController implements DeliveryApi {

    private OrderMapper orderMapper;

    private DeliveryMapper deliveryMapper;

    private DeliveryService deliveryService;

    @Autowired
    public void setDeliveryMapper(DeliveryMapper deliveryMapper) {
        this.deliveryMapper = deliveryMapper;
    }

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public ResponseEntity<DeliveryDto> createDelivery(OrderDto orderDto) {
        Order order = orderMapper.toOrder(orderDto);
        var delivery = deliveryService.createDelivery(order);
        if (delivery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deliveryMapper.toDeliveryDto(delivery.get()));
    }

    @Override
    public Mono<ResponseEntity<DeliveryDto>> getDelivery(Long deliveryId, ServerWebExchange exchange) {
        var searchResult = deliveryService.getDelivery(deliveryId);
        if (searchResult.isEmpty()) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        var deliveryDto = deliveryMapper.toDeliveryDto(searchResult.get());
        return Mono.just(ResponseEntity.ok(deliveryDto));
    }


}