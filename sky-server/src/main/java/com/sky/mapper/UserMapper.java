package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Mapper
public interface UserMapper {

    @Select("select  * from user where  openid= #{openid}")
    User getByOpenId(String openid);


    @Autowired
    Long insert(User user);
}
