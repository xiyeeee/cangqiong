package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Mapper
public interface UserMapper {

    @Select("select  * from user where  openid= #{openid}")
    User getByOpenId(String openid);

    @AutoFill(value = OperationType.INSERT)
    Long insert(User user);

    @Select("select  * from user where  id= #{userId}")
    User getById(Long userId);
}
