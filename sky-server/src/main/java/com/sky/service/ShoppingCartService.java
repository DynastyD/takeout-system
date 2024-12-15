package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @ClassName: ShoppingCartService
 * @Package:com.sky.service
 * @Description:
 * @author: Zihao
 * @date: 2024/12/12 - 16:45
 */
public interface ShoppingCartService {

    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void cleanShoppingCart();

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
