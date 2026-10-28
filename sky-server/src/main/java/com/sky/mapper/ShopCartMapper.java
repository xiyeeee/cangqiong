package com.sky.mapper;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.sky.annotation.AutoFill;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Mapper
public interface ShopCartMapper {

    @AutoFill(OperationType.UPDATE)
    void update(ShoppingCart shoppingCart);

    @AutoFill(OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);

    ShoppingCart getShopCartBySetmealId(@Param("setmealId") Long setmealId, @Param("userId") Long userId);

    ShoppingCart getShopCartByDishId(@Param("dishId") Long dishId,
                                     @Param("userId") Long userId,
                                     @Param("dishFlavor") String dishFlavor);

    List<ShoppingCart> getListByUserId(Long userId);

    void clean(Long userId);

    @Delete("delete  from shopping_cart where  id= #{id}")
    void cleanById(Long id);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
