package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
public interface DishService {

    public void save(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteById(Long id);

    Dish getById(Long id);

    void deleteByIds(List<Long> ids);

    void update(DishDTO dishDTO);

    DishVO getDishById(Long dishId);

    void updateDishStatus(Integer status, Long id);

    List<Dish> getListByCategoryId(Long categoryId);
}
