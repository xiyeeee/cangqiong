package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.ShopCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Service
@Slf4j
public class ShopCartServiceImpl implements ShopCartService {
    @Autowired
    private ShopCartMapper shopCartMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;

    private void addNumber(ShoppingCart shoppingCart) {
        shoppingCart.setNumber(shoppingCart.getNumber() + 1);
        shopCartMapper.update(shoppingCart);
    }

    private void subNumber(ShoppingCart shoppingCart) {
        Integer number = shoppingCart.getNumber();
        if (number > 1) {
            shoppingCart.setNumber(number - 1);
            shopCartMapper.update(shoppingCart);
        } else if (number == 1) {
            shopCartMapper.cleanById(shoppingCart.getId());
        }
    }

    private void shopCartInsert(ShoppingCartDTO shoppingCartDTO, Long userId, Dish dish) {
        shopCartInsert(shoppingCartDTO, userId, dish.getName(), dish.getImage(), dish.getPrice());
    }

    private void shopCartInsert(ShoppingCartDTO shoppingCartDTO, Long userId, Setmeal setmeal) {
        shopCartInsert(shoppingCartDTO, userId, setmeal.getName(), setmeal.getImage(), setmeal.getPrice());
    }

    private void shopCartInsert(ShoppingCartDTO shoppingCartDTO, Long userId,
                                String name, String image, BigDecimal price) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        shoppingCart.setNumber(1);
        shoppingCart.setName(name);
        shoppingCart.setImage(image);
        shoppingCart.setAmount(price);
        shopCartMapper.insert(shoppingCart);
    }

    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long dishId = shoppingCartDTO.getDishId();

        if (setmealId != null) {
            ShoppingCart cartItem = shopCartMapper.getShopCartBySetmealId(setmealId, userId);
            if (cartItem != null) {
                addNumber(cartItem);
                return;
            }
            Setmeal setmeal = setMealMapper.getById(setmealId);
            if (setmeal == null || Objects.equals(setmeal.getStatus(), StatusConstant.DISABLE)) {
                throw new ShoppingCartBusinessException("套餐暂未上线");
            }
            shopCartInsert(shoppingCartDTO, userId, setmeal);
        } else if (dishId != null) {
            ShoppingCart cartItem = shopCartMapper.getShopCartByDishId(
                    dishId, userId, shoppingCartDTO.getDishFlavor());
            if (cartItem != null) {
                addNumber(cartItem);
                return;
            }
            Dish dish = dishMapper.getById(dishId);
            if (dish == null || Objects.equals(dish.getStatus(), StatusConstant.DISABLE)) {
                throw new ShoppingCartBusinessException("菜品暂未上线");
            }
            shopCartInsert(shoppingCartDTO, userId, dish);
        } else {
            throw new ShoppingCartBusinessException("请选择菜品或套餐");
        }
    }

    @Override
    @Transactional
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long dishId = shoppingCartDTO.getDishId();

        if (setmealId != null) {
            ShoppingCart cartItem = shopCartMapper.getShopCartBySetmealId(setmealId, userId);
            if (cartItem != null) {
                subNumber(cartItem);
            }
        } else if (dishId != null) {
            ShoppingCart cartItem = shopCartMapper.getShopCartByDishId(
                    dishId, userId, shoppingCartDTO.getDishFlavor());
            if (cartItem != null) {
                subNumber(cartItem);
            }
        } else {
            throw new ShoppingCartBusinessException("请选择菜品或套餐");
        }
    }

    @Override
    public List<ShoppingCart> getListByUserId(Long userId) {
        return shopCartMapper.getListByUserId(userId);
    }

    @Override
    public void cleanByUserId(Long userId) {
        shopCartMapper.clean(userId);
    }
}
