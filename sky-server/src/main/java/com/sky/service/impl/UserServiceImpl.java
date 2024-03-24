package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    //将微信服务的接口地址定义为一个常量
    public static final String wx_login="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    UserMapper userMapper;


    //微信用户
    @Override
    public User userLogin(UserLoginDTO userLoginDTO) {

        String openid=getOpenid(userLoginDTO.getCode());

        //判断openids是否为空，若是表示登录失败
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断是否是新用户
        User user =userMapper.GetUerByOpenId(openid);
        if(user==null){
            //如果是自动完成注册
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }


        //返回这个用户对象
        return user;
    }

    private String getOpenid(String code){
        //调用微信接口，从微信那里获得用户信息


        Map<String, String> map=new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(wx_login, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid=jsonObject.getString("openid");

        return openid;
    }


}
