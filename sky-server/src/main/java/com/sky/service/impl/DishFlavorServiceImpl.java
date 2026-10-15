package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */

import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@Slf4j
@Service
public class DishFlavorServiceImpl implements DishFlavorService {
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    @Override
    public void save(DishFlavor dishFlavor) {
        dishFlavorMapper.insert(dishFlavor);
    }

    @Override
    public List<DishFlavor> getByDishId(Long dishId) {
        return dishFlavorMapper.getByDishId(dishId);
    }
}
