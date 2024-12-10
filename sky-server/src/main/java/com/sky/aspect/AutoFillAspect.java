package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @ClassName: AutoFillAspect
 * @Package:com.sky.aspect
 * @Description: Custom Aspects
 * @author: Zihao
 * @date: 2024/12/10 - 12:29
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    /*
    Pre-notification, assigning values to public fields in the notification
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("Start auto-filling common fields...");

        //Get the database operation type on the currently intercepted method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//Method signature object
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//get the annotation object on the method
        OperationType operationType = autoFill.value();//Get the database operation type

        //Get the method params(entity object) on the currently intercepted method
        Object[] args = joinPoint.getArgs();
        if (args ==null || args.length == 0){
            return ;
        }

        Object entity = args[0];

        //Prepare the data for assignment
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //Assign values to corresponding properties through reflection according to the current operation type
        if (operationType == OperationType.INSERT){
            //Assign values to the four public fields
            try {
                Method setCreateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //Assigning values to object properties via reflection
                setCreateTimes.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTimes.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }else if (operationType == OperationType.UPDATE){
            try {

                Method setUpdateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //Assigning values to object properties via reflection
                setUpdateTimes.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
