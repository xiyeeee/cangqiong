package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
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

    @AutoFill(value = OperationType.INSERT)
    void save(SetmealDish setmealDish);

    @Delete("delete  from   setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetMealId( Long setmealId);

    List<SetmealDish> getBySetMealId(Long id);

    void saveBatch(List<SetmealDish> setmealDishes);

    void deleteBySetMealIds(@Param("setMealIds") List<Long> setMealIds);
}
