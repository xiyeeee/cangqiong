package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Orders;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    @AutoFill(value = OperationType.INSERT)
    void insert(Orders orders);

    @Select("select *  from orders where id=#{id}")
    Orders getOrderById(Long id);
}
