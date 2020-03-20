package com.tlioylc.openannotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/126:29 PM
 * desc   :
 */
@Inherited//该注解可以被继承
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface OpenBuilder {
}


