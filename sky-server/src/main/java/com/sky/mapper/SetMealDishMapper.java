package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@Mapper
public interface SetMealDishMapper {




    @Select("select count(*) from setmeal_dish where dish_id = #{dishId}")
    SetmealDish getByDishId(Long dishId);


    Integer getDishInSetMealCountByIds(@Param("ids") List<Long> ids);
}
