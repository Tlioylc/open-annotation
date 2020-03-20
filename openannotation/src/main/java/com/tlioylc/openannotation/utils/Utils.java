package com.tlioylc.openannotation.utils;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/133:03 PM
 * desc   :
 */
public class Utils {
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String toSetMethod(String s){
        return "set"+ toUpperCaseFirstOne(s);
    }

    public static String toGetMethod(String s){
        return "get"+ toUpperCaseFirstOne(s);
    }
}
