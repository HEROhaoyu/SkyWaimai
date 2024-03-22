package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户的相关接口")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO>  login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录",userLoginDTO.getCode());

        //微信登录，小程序会给服务器端发送请求
        User user =userService.userLogin(userLoginDTO);


        //为小程序端的用户生成令牌
        HashMap<String, Object> claims = new HashMap<>();

        claims.put(JwtClaimsConstant.USER_ID,user.getId());//这里的claims是一个Map<String,Object>类型的对象
        //为微信用户生成jwt令牌
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        //按照接口文档规范封装token

        UserLoginVO userLoginVO=UserLoginVO.builder()//这里的builder()方法是lombok插件自动生成的，用于构建对象，不需要new
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }
}
