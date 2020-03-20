package com.tlioylc.openannotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited//该注解可以被继承
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Optional {
}
