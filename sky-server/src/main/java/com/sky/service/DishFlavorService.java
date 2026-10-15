package com.sky.service;

import com.sky.entity.DishFlavor;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
public interface DishFlavorService {

    public void save(DishFlavor DishFlavor);

    List<DishFlavor> getByDishId(Long dishId);
}
