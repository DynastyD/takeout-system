package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: AutoFill
 * @Package:com.sky.annotation
 * @Description: Custom annotations used to identify a method that
 *                  requires automatic filling of function fields
 * @author: Zihao
 * @date: 2024/12/10 - 12:24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //Database operation type: update, insert
    OperationType value();
}
