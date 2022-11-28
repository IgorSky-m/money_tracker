package com.bot.calculation.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * В разработке
 */
//TODO дописать аоп логирования
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Wip {
    boolean logUse() default true;
}
