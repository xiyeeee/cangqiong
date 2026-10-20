package com.sky.mapper;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Mapper
public interface ShopMapper {

    @Update("update ")
    void updateShopStatus();
}
