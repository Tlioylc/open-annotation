package com.tlioylc.openannotation.builders;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import static com.tlioylc.openannotation.utils.CommonType.TYPE_ACTIVITY;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_BUNDLE;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_LIFECYCLE_CALLBACKS;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/132:53 PM
 * desc   :
 */
public class LifecycleCallback {

    private static TypeSpec.Builder getCallBackClassBuilder() {
        TypeSpec.Builder callBackInline = TypeSpec.classBuilder("OpenActivityLifecycleCallback");
        callBackInline.addModifiers(Modifier.PUBLIC);
        callBackInline.addSuperinterface(TYPE_LIFECYCLE_CALLBACKS.get());

        callBackInline.addMethod(getEmptyActivityMethod("onActivityStarted", TYPE_ACTIVITY.get()));
        callBackInline.addMethod(getEmptyActivityMethod("onActivityResumed", TYPE_ACTIVITY.get()));
        callBackInline.addMethod(getEmptyActivityMethod("onActivityPaused", TYPE_ACTIVITY.get()));
        callBackInline.addMethod(getEmptyActivityMethod("onActivityStopped", TYPE_ACTIVITY.get()));
        callBackInline.addMethod(getEmptyActivityMethod("onActivityDestroyed", TYPE_ACTIVITY.get()));
        MethodSpec onActivitySaveInstanceState = MethodSpec.methodBuilder("onActivitySaveInstanceState")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TYPE_ACTIVITY.get(), "args0")
                .addParameter(TYPE_BUNDLE.get(), "args1")
                .build();
        callBackInline.addMethod(onActivitySaveInstanceState);
        return callBackInline;
    }

    private static MethodSpec getEmptyActivityMethod(String methodName,TypeName activityName){
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(activityName, "args0")
                .build();
    }

    public static void analysisCallBackInline(Set<? extends Element> elements, Filer mFiler) {
        TypeSpec.Builder callBackInline =  getCallBackClassBuilder();

        MethodSpec.Builder onActivityCreate = MethodSpec.methodBuilder("onActivityCreated")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TYPE_ACTIVITY.get(), "args0")
                .addParameter(TYPE_BUNDLE.get(), "args1");
        for (Element annotationElement : elements) {
            if (annotationElement.getKind() != ElementKind.CLASS) {
                continue;
            }
            com.tlioylc.openannotation.builders.BuilderClass.analysisAnnotation(annotationElement,onActivityCreate,mFiler);
        }

        callBackInline.addMethod(onActivityCreate.build());
        JavaFile javaFile = JavaFile.builder("com.tlioylc.openannotation", callBackInline.build())
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
