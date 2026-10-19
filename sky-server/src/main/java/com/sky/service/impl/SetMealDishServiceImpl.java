package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */

import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@Slf4j
@Service
public class SetMealDishServiceImpl implements SetMealDishService {
    @Autowired
    SetMealDishMapper setMealDishMapper;


    @Override
    public SetmealDish getByDishId(Long dishId) {
        return setMealDishMapper.getByDishId(dishId);
    }

    @Override
    public void deleteBySetmealId(Long setmealId) {

    }

    @Override
    public List<SetmealDish> getBySetmealId(Long setmealId) {
        return Collections.emptyList();
    }
}
