package com.sky.controller.admin;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/14
 */
@RestController
@Api(tags = "菜品管理")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    /**
     *
     */
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private void deleteDishRedis(){
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {

        dishService.save(dishDTO);
        deleteDishRedis();
        return Result.success();

    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {

        dishService.update(dishDTO);
        deleteDishRedis();
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result updateDishStatus(@PathVariable Integer status, Long id) {

        dishService.updateDishStatus(status, id);
        deleteDishRedis();
        return Result.success();
    }


    @ApiOperation("查看菜品")
    @GetMapping("/{dishId}")
    public Result<DishVO> getDishById(@PathVariable Long dishId) {
        DishVO dishVO = dishService.getDishById(dishId);
        return Result.success(dishVO);
    }

    @ApiOperation("分页")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

//    @ApiOperation("删除菜品")
//    @DeleteMapping()
//    public Result deleteById(Long id){
//        dishService.deleteById(id);
//        return Result.success();
//    }

    @ApiOperation("删除菜品")
    @DeleteMapping()
    public Result deleteById(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的菜品");
        }
        dishService.deleteByIds(ids);
        deleteDishRedis();
        return Result.success();
    }

    @ApiOperation("菜品下拉")
    @GetMapping("/list")
    public Result<List> pageQuery(@RequestParam Long categoryId) {
        List<Dish> list = dishService.getListByCategoryId(categoryId);
        return Result.success(list);
    }

}
