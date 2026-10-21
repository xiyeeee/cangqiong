package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    SetMealMapper setMealMapper;

    @Autowired
    SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.save(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {

            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setMealDishMapper.saveBatch(setmealDishes);
        }
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        Long setmealId = setmealDTO.getId();
        if (setmealId == null) {
            throw new BaseException("id不存在");
        }
        Setmeal setmealEntity = setMealMapper.getById(setmealId);
        if (setmealEntity == null) {
            throw new BaseException("id不存在");
        }

        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);
        setMealDishMapper.deleteBySetMealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setMealDishMapper.saveBatch(setmealDishes);
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            List<SetmealDish> setMealDishList = setMealDishMapper.getBySetMealId(id);
            if (setMealDishList != null && !setMealDishList.isEmpty()) {
                for (SetmealDish setmealDish : setMealDishList) {
                    Dish dish = dishMapper.getById(setmealDish.getDishId());
                    if (dish != null && Objects.equals(dish.getStatus(), StatusConstant.DISABLE)) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }

        }
        setmeal.setStatus(status);
        setMealMapper.update(setmeal);
    }

    @Override
    public SetmealDTO getSetMealById(Long id) {
        SetmealDTO setmealDTO = new SetmealDTO();
        Setmeal setMeal = setMealMapper.getById(id);
        if (setMeal != null) {
            BeanUtils.copyProperties(setMeal, setmealDTO);
        }
        List<SetmealDish> setMealDishList = setMealDishMapper.getBySetMealId(id);
        setmealDTO.setSetmealDishes(setMealDishList);
        return setmealDTO;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        Integer onSaleMealCount = setMealMapper.getOnSaleMealCount(ids, StatusConstant.ENABLE);
        if (onSaleMealCount != null && onSaleMealCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SEATMEAL_ON_SEAL);
        }
        setMealMapper.deleteByIds(ids);
        setMealDishMapper.deleteBySetMealIds(ids);
    }


    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}
