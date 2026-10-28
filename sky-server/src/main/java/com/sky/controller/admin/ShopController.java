package com.sky.controller.admin;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/19
 */

import com.alibaba.fastjson.JSONObject;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/19
 */
@RestController("adminShopController")

@Slf4j
@Api(tags = "营业相关")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private WebSocketServer webSocketServer;
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    ShopCartService shopService;
    @Autowired
    private RedisTemplate redisTemplate;


    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result updateShopStatus(@PathVariable Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
        HashMap hashMap = new HashMap();
        hashMap.put("type", 1);
        hashMap.put("orderId", 1);
        hashMap.put("content", "订单号" + 1);

        webSocketServer.sendToAllClient(JSONObject.toJSONString(hashMap));
        return Result.success();
    }


    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getShopStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }
}
