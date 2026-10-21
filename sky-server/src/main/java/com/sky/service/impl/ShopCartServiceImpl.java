package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.ShopCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Service
@Slf4j
public class ShopCartServiceImpl implements ShopCartService {
    @Autowired
    ShopCartMapper shopCartMapper;


    @Override
    public void updateShopStatus(Integer status) {
        shopCartMapper.updateShopStatus();
    }

    private static final  Long userId = BaseContext.getCurrentId();

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        // 如果有重复, 则数量+1 , 菜品的重复要判断口味
        ShoppingCart shoppingCartSetmealItem =  shopCartMapper.getShopCartBySetmealId(shoppingCartDTO.getSetmealId(),userId);
        if(shoppingCartSetmealItem !=null){
            Integer number = shoppingCartSetmealItem.getNumber();
            shoppingCart.setNumber(number+1);
            shoppingCart.setId(shoppingCartSetmealItem.getId());
            shopCartMapper.updateShopCart()
        }
        // 套餐和单品 不能同时存在
        //


        //
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {

    }

    @Override
    public List<ShoppingCart> getListByUserId(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public void cleanByUserId(Long userId) {

    }
}
