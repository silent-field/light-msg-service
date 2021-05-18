package com.silent.lms.annotations;

import java.lang.annotation.*;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description:
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ThreadSafe {
    String value() default "";
}