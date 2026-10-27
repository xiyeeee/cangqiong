package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    WeChatProperties weChatProperties;

    public static final String WX_LOGIN = "http://api.weixin.qq.com/sns/jscode2session";

    private String getOpenId(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        // 微信接口获取用户
        String openId = getOpenId(userLoginDTO.getCode());

        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        }
        // 判断是否是新用户


        User user = userMapper.getByOpenId(openId);

        if (user == null) {
            user = User.builder().openid(openId).build();
            userMapper.insert(user);
        }


        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder().id(user.getId()).openid(user.getOpenid()).token(token).build();
        return userLoginVO;
    }
}
