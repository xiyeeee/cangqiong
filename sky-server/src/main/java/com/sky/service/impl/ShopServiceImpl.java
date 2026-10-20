package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.sky.mapper.ShopMapper;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    @Autowired
    ShopMapper shopMapper;


    @Override
    public void updateShopStatus(Integer status) {
        shopMapper.updateShopStatus();
    }
}
