package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<Dish> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void deleteById(Long id) {
        dishMapper.deleteById(id);
    }

    @Override
    public Dish getById(Long id) {
        Dish dish = dishMapper.getById(id);
        return dish;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // for (Long id : ids) {
        //     Dish dish = dishMapper.getById(id);
        //     if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
        //         throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        //     }
        //     SetmealDish setmealDish = setMealDishMapper.getByDishId(id);
        //     if(setmealDish!=null){
        //         throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        //     }

        // }

        Integer enableDishCount = dishMapper.getEnableDishCountInIds(ids, StatusConstant.ENABLE);
        Integer dishInSetMealCount = setMealDishMapper.getDishInSetMealCountByIds(ids);
        if (enableDishCount != null && enableDishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        if (dishInSetMealCount != null && dishInSetMealCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 绑定了套餐的也不允许删除
        dishFlavorMapper.deleteByDishIds(ids);
        dishMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        Long id = dishDTO.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(id));
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            // 这里先删后插

            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public DishVO getDishById(Long dishId) {
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(dishId);
        List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(dishId);
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    @Override
    public void updateDishStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

}
