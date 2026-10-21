package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
public interface ShopCartService {

    void add(ShoppingCartDTO shoppingCartDTO);

    void sub(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> getListByUserId(Long userId);

    void cleanByUserId(Long userId);
}
