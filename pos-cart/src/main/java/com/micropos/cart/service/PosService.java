package com.micropos.cart.service;


import com.micropos.dto.OrderDto;
import com.micropos.model.Cart;
import com.micropos.model.Item;
import com.micropos.model.Order;
import com.micropos.model.Product;
import org.springframework.web.server.WebSession;

import java.util.List;

public interface PosService {


    void checkout(Cart cart);

    Item add(Product product, int amount, Cart cart, WebSession webSession);

    Item add(Integer productId, int amount, Cart cart, WebSession webSession);

//    List<Product> products();

//    Product randomProduct();

    Item getItem(Integer productId, Cart cart);

    Item removeItem(Integer ProductId, Cart cart, WebSession webSession);

    Item modifyItem(Integer productId, int quantity, Cart cart, WebSession webSession);


    OrderDto createOrder(Cart cart);
}
