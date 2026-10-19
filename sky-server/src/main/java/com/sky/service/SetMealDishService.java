package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
public interface SetMealDishService {


    SetmealDish getByDishId(Long dishId);

    void deleteBySetmealId(Long setmealId);

    List<SetmealDish> getBySetmealId(Long setmealId);


}
