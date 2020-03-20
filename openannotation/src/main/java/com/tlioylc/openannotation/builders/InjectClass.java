package com.tlioylc.openannotation.builders;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import static com.tlioylc.openannotation.utils.CommonType.TYPE_ACTIVITY;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_BUNDLE;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_FIELD;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_INTENT;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/132:52 PM
 * desc   :
 */
public class InjectClass {
    public static void analysisAnnotationInject(List<VariableElement> allElements, ClassName targetActivity, String className, String packageName, Filer mFiler) {
        TypeSpec.Builder injectClass = TypeSpec.classBuilder(className);
        injectClass.addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder valSpec = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TYPE_ACTIVITY.get(), "activity")
                .addParameter(TYPE_BUNDLE.get(), "savedInstanceState")
                .addCode(" if(activity instanceof $T){", targetActivity)
                .addCode("$T instance = (($T) activity);", targetActivity, targetActivity)
                //暂不考虑页面重构的情况
//                .addCode("if(savedInstanceState != null){")
//                .addCode("}else{")
                .addCode("$T intent = instance.getIntent();\n" +
                        "                if(intent == null){\n" +
                        "                    return;\n" +
                        "                } \n ", TYPE_INTENT.get());

        for (VariableElement element : allElements) {
            handleValTypeGet(valSpec, element);
        }
//        valSpec.addCode("}\n");
        valSpec.addCode("}\n");
        injectClass.addMethod(valSpec.build());
        JavaFile javaFile = JavaFile.builder(packageName, injectClass.build())
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void handleValTypeGet(MethodSpec.Builder valSpec, VariableElement element) {
        TypeName valType = ClassName.get(element.asType());
        String valTypeName = valType.toString();

        String fieldName = "field" + element.getSimpleName();
        String valueName = "value" + element.getSimpleName();

        if (int.class.getCanonicalName().equals(valTypeName) || Integer.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("int  $N  = intent.getIntExtra(\"$N\",-1)", valueName, element.getSimpleName());
        } else if (double.class.getCanonicalName().equals(valTypeName) || Double.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("double  $N  = intent.getDoubleExtra(\"$N\",-1)", valueName, element.getSimpleName());
        } else if (boolean.class.getCanonicalName().equals(valTypeName) || Boolean.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("boolean  $N = intent.getBooleanExtra(\"$N\",false)", valueName, element.getSimpleName());
        } else if (long.class.getCanonicalName().equals(valTypeName) || Long.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("long  $N  = intent.getLongExtra(\"$N\",-1L)", valueName, element.getSimpleName());
        } else if (float.class.getCanonicalName().equals(valTypeName) || Float.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("float  $N = intent.getFloatExtra(\"$N\",-1f)", valueName, element.getSimpleName());
        } else if (String.class.getCanonicalName().equals(valTypeName)) {
            valSpec.addStatement("String  $N = intent.getStringExtra(\"$N\")", valueName, element.getSimpleName());
        } else {
            valSpec.addStatement("$T $N = intent.getStringExtra(\"$N\")", String.class, element.getSimpleName() + "Json", element.getSimpleName());
            TypeName jsonType = ClassName.get("com.alibaba.fastjson", "JSON");
            valSpec.addStatement("$T  $N =  $T.parseObject($N,$T.class)",valType, valueName, jsonType, element.getSimpleName() + "Json", valType);
        }


        valSpec.addCode("  $T  $N = null; \n" +
                "         try {\n" +
                "             $N = instance.getClass().getDeclaredField(\"$N\");\n" +
                "             $N.setAccessible(true); \n" +
                "             $N.set(instance,  $N); \n" +
                "         } catch (NoSuchFieldException e) {\n" +
                "             e.printStackTrace();\n" +
                "         } catch (IllegalAccessException e) {\n" +
                "             e.printStackTrace();\n" +
                "         }", TYPE_FIELD.get() ,fieldName,fieldName,element.getSimpleName(),fieldName,fieldName ,valueName);
    }
}
