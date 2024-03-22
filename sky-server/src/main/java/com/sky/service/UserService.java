package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;


public interface UserService {


    //微信登录接口
    User userLogin(UserLoginDTO userLoginDTO);
}
