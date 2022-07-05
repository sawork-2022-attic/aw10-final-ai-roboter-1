package com.micropos.counter.rest;

import com.micropos.api.BillApi;
import com.micropos.counter.service.CounterService;
import com.micropos.dto.MessageDto;
import com.micropos.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class CounterController implements BillApi {

    private CounterService counterService;

    @Autowired
    public void setCounterService(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public Mono<ResponseEntity<MessageDto>> createBill(Mono<OrderDto> orderDtoMono, ServerWebExchange exchange) {
        var IdMono = orderDtoMono.map(OrderDto::getId).map(Math::toIntExact);
        return counterService.createBill(IdMono).map(result -> {
            MessageDto messageDto = new MessageDto();
            if (result) {
                messageDto.message("创建成功");
                messageDto.setSuccess(true);
            } else {
                messageDto.message("创建失败");
                messageDto.setSuccess(false);
            }
            return ResponseEntity.ok(messageDto);
        });
    }

}
