package com.tlioylc.openannotation.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/132:54 PM
 * desc   :
 */
public enum  CommonType {
    TYPE_ACTIVITY(ClassName.get("android.app", "Activity")),
    TYPE_BUNDLE(ClassName.get("android.os", "Bundle")),
    TYPE_LIFECYCLE_CALLBACKS(ClassName.get("android.app.Application", "ActivityLifecycleCallbacks")),
    TYPE_CONTEXT(ClassName.get("android.content", "Context")),
    TYPE_CONTEXT_WRAPPER(ClassName.get("android.content", "ContextWrapper")),
    TYPE_INTENT(ClassName.get("android.content", "Intent")),
    TYPE_JSON(ClassName.get("com.alibaba.fastjson","JSON")),
    TYPE_FIELD(ClassName.get("java.lang.reflect","Field"));

    private TypeName type;
    CommonType(TypeName type){
        this.type = type;
    }

    public TypeName get() {
        return type;
    }
}
