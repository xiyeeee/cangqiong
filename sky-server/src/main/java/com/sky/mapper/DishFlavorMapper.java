package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
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
public interface DishFlavorMapper {
    @Select("select  * from dish where  name =#{name}")
    Category selectByName(String name);

    void insert(DishFlavor dishFlavor);

    void insertBatch(List<DishFlavor> flavors);

    void deleteByDishIds(@Param("ids") List<Long> ids);

    @Select("select  * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    @AutoFill(value = OperationType.UPDATE)
    void updateBatch(@Param("flavors") List<DishFlavor> flavors);
}
