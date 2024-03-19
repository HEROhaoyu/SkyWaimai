package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义切面，实现公共字段的自动填充处理逻辑
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}


    //自定义的前置通知,在通知中为公共字段赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的自动填充……");
        //获取需要进行操作的类型：insert? update?
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();//获得连接点对象,转为方法的连接点
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = annotation.value();//获得进行操作的类型
        //获得被连接方法的实参，找到我们需要填充的数据来源，我们约定方法的第一个实参为我们所需的,注意，这只是个约定，需要程序员去遵守
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0){ return;}

        Object entry=args[0];

        //获取需要被赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long current_id= BaseContext.getCurrentId();//我们在令牌拦截器中已经将id存入threadLocal

        //根据操作类型的不同，执行不同操作。使用反射来对属性进行重赋值
        try {
            Method setCreateTime = entry.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entry.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

            Method setCreateUser = entry.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entry.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            if(operationType==OperationType.INSERT){
                //通过反射来为对象属性赋值
                setCreateTime.invoke(entry,now);
                setUpdateTime.invoke(entry,now);
                setCreateUser.invoke(entry,current_id);
                setUpdateUser.invoke(entry,current_id);
            }
            else if(operationType==OperationType.UPDATE){
                setUpdateTime.invoke(entry,now);
                setUpdateUser.invoke(entry,current_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
