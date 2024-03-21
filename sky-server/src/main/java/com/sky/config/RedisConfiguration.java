package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {//设置一个配置类，用于生成redis的模板的bean对象


    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象");
        RedisTemplate redisTemplate=new RedisTemplate();

        //设置连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置redis的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return  redisTemplate;
    }

}
