package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from sky_take_out.user where openid=#{openid}")
    User GetUerByOpenId(String openid);


    void insert(User user);//插入数据后，需要得到插入的用户的id，这个功能比较复杂，需要使用动态sql

    @Select("select * from sky_take_out.user where id=#{id}")
    User getById(Long userId);

    @Select("select count(id) from sky_take_out.user where create_time<#{endTime}")
    Integer countByMap(Map useMap);
}
