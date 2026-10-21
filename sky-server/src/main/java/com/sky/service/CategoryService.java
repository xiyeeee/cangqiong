package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/12
 */

public interface CategoryService {

    void save(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateStatus(Integer status, Long id);

    void deleteById(Long id);

    List<Category> getListByType(Integer type);

    List<Category> getList();
}
