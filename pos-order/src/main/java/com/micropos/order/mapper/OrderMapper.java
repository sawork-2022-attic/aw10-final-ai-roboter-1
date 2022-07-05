package com.micropos.order.mapper;

import com.micropos.dto.CartDto;
import com.micropos.dto.ItemDto;
import com.micropos.dto.OrderDto;
import com.micropos.dto.ProductDto;
import com.micropos.model.Cart;
import com.micropos.model.Item;
import com.micropos.model.Order;
import com.micropos.model.Product;
import org.aspectj.weaver.ast.Or;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Mapper
public interface OrderMapper {
    default OrderDto toOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.id().longValue());
        var cart = new Cart();
        var items = new ArrayList<Item>();
        for (var orderItem: order.getOrderItems()) {
            var item = new Item();
            item.product(orderItem.product());
            item.quantity(orderItem.quantity());
            items.add(item);
        }
        cart.setItems(items);
        orderDto.setCart(toCartDto(cart));
        CartDto cartDto = new CartDto();
        cartDto.setId(0L);
        cartDto.setItems(cart.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
        orderDto.setCart(cartDto);
        return orderDto;
    }

    Order toOrder(OrderDto orderDto);

    default CartDto toCartDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(0L);
        cartDto.setItems(cart.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
        return cartDto;
    }


    default Cart toCart(CartDto cartDto) {
        var cart = new Cart();
        cart.setItems(cartDto.getItems().stream().map(this::toItem).collect(Collectors.toList()));
        return cart;
    };

    default ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setProduct(this.toProductDto(item.product()));
        itemDto.setQuantity(item.quantity());
        return itemDto;
    };

    Collection<Product> toProducts(Collection<ProductDto> products);

    default Product toProduct(ProductDto productDto) {
        var product = new Product();
        product.id(productDto.getId()).price(productDto.getPrice().doubleValue()).
                image(productDto.getImage()).name(productDto.getName());
        return product;
    };

    default ProductDto toProductDto(Product product) {
        return new ProductDto().id(product.id()).price(BigDecimal.valueOf(product.price()))
                .name(product.name()).image(product.image());
    }
    default Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.product(toProduct(itemDto.getProduct()));
        item.quantity(itemDto.getQuantity());
        return item;
    };

//    Item toItem(ItemFiledDto itemFiledDto);

    public List<ItemDto> toItemDtos(Collection<Item> items);
}
