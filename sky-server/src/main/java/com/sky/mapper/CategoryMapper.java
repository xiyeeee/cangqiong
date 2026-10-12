package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/12
 */
@Mapper
public interface CategoryMapper {

    @Insert("insert into category" +
            " (name, type,sort ,status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{type},#{sort},#{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);

    @Select("select  * from category where  name =#{name}")
    Category selectByName(String name);

    @Select("select  * from category where  id =#{id}")
    Category selectById(String name);


    void update(Category category);


    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @Delete( "delete  from category where  id =#{id}")
    void deleteById(Long id);
}
