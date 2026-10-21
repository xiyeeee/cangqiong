package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "购物车")
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShopCartController {
    @Autowired
     ShopCartService shopCartService;


    /**
     * 新增
     *
     */

    @PostMapping("/add")
    public Result add(ShoppingCartDTO shoppingCartDTO){
        shopCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 数量减1
     */
    @PostMapping("/sub")
    public Result sub(ShoppingCartDTO shoppingCartDTO){
        shopCartService.sub(shoppingCartDTO);
        return Result.success();
    }
    /**
     * 查看
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>>   getList(){
        Long userId = BaseContext.getCurrentId();

        List<ShoppingCart> shoppingCartList =   shopCartService.getListByUserId(userId);
        return Result.success(shoppingCartList);
    }
    /**
     * 清空
     */
    @DeleteMapping("/clean")
    public Result clean(){
        Long userId = BaseContext.getCurrentId();
        shopCartService.cleanByUserId(userId);
        return Result.success();
    }


}
