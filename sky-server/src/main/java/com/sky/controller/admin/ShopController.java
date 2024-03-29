package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    @Autowired
    RedisTemplate redisTemplate;

    /*设置店铺状态*/
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态为：{}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set("店铺状态",status);



        return Result.success();
    }

    /*查询店铺状态*/
    @GetMapping("/status")
    @ApiOperation("管理员查询店铺状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get("店铺状态");
        log.info("查询店铺状态为：{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
