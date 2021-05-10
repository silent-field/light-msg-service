package com.silent.lms.annotations;

import java.lang.annotation.*;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description:
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface NotNull {
    String value() default "";
}