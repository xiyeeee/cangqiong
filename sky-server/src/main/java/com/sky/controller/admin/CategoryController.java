package com.sky.controller.admin;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/12
 */

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/12
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
/**
 *
 */
    @ApiOperation("新增分类")
    @PostMapping()
    public Result save(@RequestBody CategoryDTO categoryDTO){
        categoryService.save(categoryDTO);
        return Result.success();
    }


    @ApiOperation("修改分类")
    @PutMapping()
    public Result update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @ApiOperation("分页")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult =   categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, Long id){
        categoryService.updateStatus(status,id);
        return Result.success();
    }

    @ApiOperation("删除分类")
    @DeleteMapping()
    public Result deleteById(Long id){
        categoryService.deleteById(id);
        return Result.success();
    }


    @ApiOperation("根据类型查询分类")
    @GetMapping("list")
    public Result<List<Category>> getListByType(Integer type){
        List<Category> list = categoryService.getListByType(type);
        return Result.success(list);
    }
}
