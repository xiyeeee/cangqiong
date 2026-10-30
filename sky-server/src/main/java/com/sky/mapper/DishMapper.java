package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@Mapper
public interface DishMapper {


    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Delete("delete from dish where id =#{id}")
    void deleteById(Long id);

    void deleteByIds(@Param("ids") List<Long> ids);

    @Select("select  * from dish where  id= #{id}")
    Dish getById(Long id);

    Integer getEnableDishCountInIds(@Param("ids") List<Long> ids, @Param("status") Integer status);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getListByCategoryId(Long categoryId);

    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     */
    Integer countByMap(Map map);
}
