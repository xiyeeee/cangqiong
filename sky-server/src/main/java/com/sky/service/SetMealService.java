package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface SetMealService {
    void save(SetmealDTO setmealDTO);
    void update(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void updateStatus(Integer status, Long id);

    SetmealDTO getSetMealById(Long id);

    void deleteByIds(List<Long> ids);
}
