package com.sky.controller.admin;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "套餐管理")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    SetMealService setMealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setMealService.save(setmealDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setMealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    public Result updateStatus(@PathVariable Integer status ,Long id){
        setMealService.updateStatus(status,id);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("套餐分页")
    public Result<PageResult>  pageQuery( SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult  pageResult= setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("套餐详情")
    @GetMapping("/{id}")
    public Result<SetmealDTO> getSetMealById(@PathVariable Long id){
        SetmealDTO setmealDTO =   setMealService.getSetMealById(id);
        return  Result.success(setmealDTO);
    }

    @DeleteMapping()
    @ApiOperation("删除套餐")
    public Result deleteByIds(@RequestParam List<Long> ids){
        setMealService.deleteByIds(ids);
        return Result.success();
    }


}
