package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional //由于这里的修改会涉及多个表格，为了保证不同表的数据一直性，这里加上事务的注解
    public void saveWithFlavor(DishDTO dishDTO) {
        //首先向菜品表传一个 dish对象,需要先传一个数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(!flavors.isEmpty()&&flavors.size()>0){//这里要对为空或者长度为0的情况分别进行判断，这是两种不同的情况

            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //批量插入口味数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
