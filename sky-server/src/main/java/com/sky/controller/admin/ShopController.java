package com.sky.controller.admin;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/19
 */

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/19
 */
@RestController
@Slf4j
@Api(tags = "营业相关")
@RequestMapping("/admin/shop")
public class ShopController {

    @PutMapping("/{status}")
    public Result updateShopStatus(@PathVariable Integer status) {

    }
}
