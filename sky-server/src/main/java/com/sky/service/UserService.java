package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @ClassName: UserService
 * @Package:com.sky.service
 * @Description:
 * @author: Zihao
 * @date: 2024/12/11 - 22:39
 */
public interface UserService {



    User wxLogin(UserLoginDTO userLoginDTO);
}
