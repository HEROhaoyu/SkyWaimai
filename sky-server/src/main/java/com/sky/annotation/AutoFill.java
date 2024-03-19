package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义注解，用于对频繁使用的公共字段的自动填充
@Target({ElementType.METHOD})//指定当前的注解可以加到什么位置
@Retention(RetentionPolicy.RUNTIME)//指示注解的生命周期，有的是在运行时，有的是在编译阶段
public @interface AutoFill {
    OperationType value(); //添加一个属性，指定当前操作的类型.注解的特殊语法：属性名后加（）

}
