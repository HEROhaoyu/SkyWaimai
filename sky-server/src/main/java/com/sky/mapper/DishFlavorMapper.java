package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);//批量化插入口味文件

    @Delete("delete from sky_take_out.dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long id);


    @Select("select * from sky_take_out.dish_flavor where dish_id=#{dish_id}")
    List<DishFlavor> getByDishId(Long dishId);
}
