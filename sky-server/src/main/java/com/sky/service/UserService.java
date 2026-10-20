package com.sky.service;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/20
 */
public interface UserService {

    UserLoginVO login(UserLoginDTO userLoginDTO);
}
