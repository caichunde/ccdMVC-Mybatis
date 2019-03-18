package com.test.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Target({ElementType.TYPE, FIELD, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value()default "";
}
