package com.micropos.delivery.mapper;

import com.micropos.dto.DeliveryDto;
import com.micropos.model.Delivery;
import org.mapstruct.Mapper;

@Mapper
public interface DeliveryMapper{
    Delivery toDelivery(DeliveryDto deliveryDto);

    default DeliveryDto toDeliveryDto(Delivery delivery) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.id(delivery.id()).cart(1);
        return deliveryDto;
    };
}
