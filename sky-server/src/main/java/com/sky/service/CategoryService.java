package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/12
 */

public interface CategoryService {

    void save(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateStatus( Integer status,Long id);

    void deleteById(Long id);

    void getListByType( Integer type);
}
