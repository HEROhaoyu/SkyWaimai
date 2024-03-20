package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.mapper.SetMealDishMapper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    SetMealDishMapper setMealDishMapper;

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

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否处于 起售 状态
        for (Long id : ids) {
            Dish dish = dishMapper.getByID(id);
            if(dish.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否被套餐关联
        List<Long> setMealIdss = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if(setMealIdss!=null&&setMealIdss.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }



        for (Long id : ids) {
            //删除菜品中的菜品数据
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }


    }

    @Override
    public DishVO getDishById(Long id) {
        //处理查询菜品，还要查关联的口味
        //1，根据id查菜品数据
        Dish dish=dishMapper.getByID(id);


        //2,根据菜品id查询口味数据
        List<DishFlavor> dishFlavors= dishFlavorMapper.getByDishId(id);

        //3,根据查询到的数据封装到VO
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public void updateWithFalvor(DishDTO dishDTO) {
        //修改口味=先删除口味数据，再新增口味数据
        //修改菜品基本信息
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);//这个工具不仅能把大数据传到小数据，还能将小数据传到大数据。它有两个参数，分别代表拷贝的源头和目的地
        dishMapper.update(dish);

        //删除口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //添加新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(!flavors.isEmpty()&&flavors.size()>0){//这里要对为空或者长度为0的情况分别进行判断，这是两种不同的情况

            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));//根据接口文档，这里没有传来菜品id，需要重新设置一下
            //批量插入口味数据
            dishFlavorMapper.insertBatch(flavors);

        }


        //
    }
}
