package com.tlioylc.openannotation.builders;


import com.tlioylc.openannotation.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.tlioylc.openannotation.utils.CommonType.TYPE_ACTIVITY;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_CONTEXT;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_CONTEXT_WRAPPER;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_INTENT;
import static com.tlioylc.openannotation.utils.CommonType.TYPE_JSON;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/133:06 PM
 * desc   :
 */
public class BuilderClass {

    private static boolean isBasicValType(String valTypeName) {
        return int.class.getCanonicalName().equals(valTypeName)
                || Integer.class.getCanonicalName().equals(valTypeName)
                || double.class.getCanonicalName().equals(valTypeName)
                || Double.class.getCanonicalName().equals(valTypeName)
                || boolean.class.getCanonicalName().equals(valTypeName)
                || Boolean.class.getCanonicalName().equals(valTypeName)
                || long.class.getCanonicalName().equals(valTypeName)
                || Long.class.getCanonicalName().equals(valTypeName)
                || float.class.getCanonicalName().equals(valTypeName)
                || Float.class.getCanonicalName().equals(valTypeName)
                || String.class.getCanonicalName().equals(valTypeName);
    }

    private static void initRequireMethod(String newClassName, String packageName, TypeSpec.Builder builderClass, MethodSpec.Builder flux, List<VariableElement> requireList) {
        //TODO 必须设置内容参数
        MethodSpec.Builder initSpec = MethodSpec.methodBuilder("init");
        initSpec.addStatement("$T instance =  new $T()", ClassName.get(packageName, newClassName), ClassName.get(packageName, newClassName));
        initSpec.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        for (Element requireElement : requireList) {
            TypeName valType = ClassName.get(requireElement.asType());
            //TODO  intent无法传输非基本类型 ，考虑fastjson转换
            if (isBasicValType(valType.toString())) {
                flux.addStatement("intent.putExtra(\"$N\",$N)", requireElement.getSimpleName().toString(), requireElement.getSimpleName().toString());
            } else {
                flux.addStatement("intent.putExtra(\"$N\",$T.toJSONString($N))", requireElement.getSimpleName().toString(), TYPE_JSON.get(), requireElement.getSimpleName().toString());
            }

            builderClass.addField(valType, requireElement.getSimpleName().toString(), Modifier.PRIVATE);

            initSpec.addParameter(valType, requireElement.getSimpleName().toString())
                    .addStatement("instance.$N = $N", requireElement.getSimpleName().toString(), requireElement.getSimpleName().toString());
        }
        initSpec.addStatement("return instance", ClassName.get(packageName, newClassName))
                .returns(ClassName.get(packageName, newClassName));
        builderClass.addMethod(initSpec.build());
    }

    private static void initOptionalMethod(String newClassName, String packageName, List<? extends Element> childElements, TypeSpec.Builder builderClass, MethodSpec.Builder flux, List<VariableElement> requireList, List<VariableElement> allElements) {
        for (Element annotationElement : childElements) {
            if (annotationElement.getKind() != ElementKind.FIELD) {
                continue;
            }
            VariableElement optionalElement = (VariableElement) annotationElement;
            Annotation annotation = optionalElement.getAnnotation(com.tlioylc.openannotation.annotation.Require.class);
            if (annotation != null) {
                allElements.add(optionalElement);
                requireList.add(optionalElement);
                continue;
            }
            Annotation annotation2 = optionalElement.getAnnotation(com.tlioylc.openannotation.annotation.Optional.class);
            if (annotation2 == null) {
                continue;
            }
            allElements.add(optionalElement);
            TypeName valType = ClassName.get(optionalElement.asType());
            if (isBasicValType(valType.toString())) {
                flux.addStatement("intent.putExtra(\"$N\",$N)", optionalElement.getSimpleName().toString(), optionalElement.getSimpleName().toString());
            } else {
                flux.addStatement("intent.putExtra(\"$N\",$T.toJSONString($N))", optionalElement.getSimpleName().toString(), TYPE_JSON.get(), optionalElement.getSimpleName().toString());
            }

            builderClass.addField(valType, optionalElement.getSimpleName().toString(), Modifier.PRIVATE);
            MethodSpec valSpec = MethodSpec.methodBuilder(Utils.toSetMethod(optionalElement.getSimpleName().toString()))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(valType, optionalElement.getSimpleName().toString())
                    .addStatement("this.$N = $N", optionalElement.getSimpleName().toString(), optionalElement.getSimpleName().toString())
                    .addStatement("return this")
                    .returns(ClassName.get(packageName, newClassName))
                    .build();
            builderClass.addMethod(valSpec);
        }
    }


    public static void analysisAnnotation(Element classElement, final MethodSpec.Builder injectMethod, Filer mFiler) {
        TypeElement typeElement = (TypeElement) classElement;
        String newClassName = typeElement.getSimpleName().toString() + "Builder";
        String qualifiedName = typeElement.getQualifiedName().toString();
        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf(typeElement.getSimpleName().toString()) - 1);

        List<? extends Element> childElements = classElement.getEnclosedElements();
        //TODO 设置类
        TypeSpec.Builder builderClass = TypeSpec.classBuilder(newClassName);
        builderClass.addModifiers(Modifier.PUBLIC);

        //TODO 设置页面打开方式
        setOpenType(newClassName, packageName, builderClass);

        //TODO 添加获取activity工具类
        getActivityByContextMethod(builderClass);

        ClassName targetActivity = ClassName.get(packageName, typeElement.getSimpleName().toString());
        MethodSpec.Builder flux = MethodSpec.methodBuilder("open")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TYPE_CONTEXT.get(), "context")
                .addStatement("$T intent = new $T($N,$T.class)", TYPE_INTENT.get(), TYPE_INTENT.get(), "context", targetActivity);

        //TODO 必须参数列表
        List<VariableElement> requireList = new ArrayList<>();
        //TODO 所有的参数列表
        List<VariableElement> allElements = new ArrayList<>();
        //TODO 非必须设置内容参数
        initOptionalMethod(newClassName, packageName, childElements, builderClass, flux, requireList, allElements);
        initRequireMethod(newClassName, packageName, builderClass, flux, requireList);

        flux.addCode("Activity activity = getActivity(context);\n" +
                "if (activity != null){\n" +
                "  if(requestCode != 0){\n" +
                "     activity.startActivityForResult(intent, requestCode);\n" +
                "  }else{\n" +
                "     activity.startActivity(intent);\n" +
                "  } \n" +
                "} else{\n" +
                "  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n" +
                "  context.startActivity(intent);\n" +
                "}");

        builderClass.addMethod(flux.build());
        JavaFile javaFile = JavaFile.builder(packageName, builderClass.build())
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String injectClassName = typeElement.getSimpleName().toString() + "Inject";
        InjectClass.analysisAnnotationInject(allElements, targetActivity, injectClassName, packageName, mFiler);

        injectMethod.addStatement("$T.inject(args0,args1)", ClassName.get(packageName, injectClassName));
    }

    private static void getActivityByContextMethod(TypeSpec.Builder builderClass) {
        MethodSpec getActivityMethod = MethodSpec.methodBuilder(Utils.toGetMethod("activity"))
                .addModifiers(Modifier.PRIVATE)
                .addParameter(TYPE_CONTEXT.get(), "context")
                .returns(TYPE_ACTIVITY.get())
                .addCode("if(context instanceof $T)\n"
                        + "  return ($T) context;\n", TYPE_ACTIVITY.get(), TYPE_ACTIVITY.get())
                .addStatement("$T funContext = context", TYPE_CONTEXT.get())
                .addCode("while (funContext instanceof $T) {\n" +
                        "      if (funContext instanceof Activity) {\n" +
                        "        return (Activity) funContext;\n" +
                        "      }\n" +
                        "       funContext = ((ContextWrapper) funContext).getBaseContext();\n" +
                        "    }\n" +
                        " return null;", TYPE_CONTEXT_WRAPPER.get())
                .build();
        builderClass.addMethod(getActivityMethod);
    }


    private static void setOpenType(String newClassName, String packageName, TypeSpec.Builder builderClass) {
        builderClass.addField(int.class, "requestCode", Modifier.PRIVATE);
        MethodSpec requestMethod = MethodSpec.methodBuilder(Utils.toSetMethod("requestCode"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "requestCode")
                .addStatement("this.requestCode = requestCode")
                .addStatement("return this")
                .returns(ClassName.get(packageName, newClassName))
                .build();
        builderClass.addMethod(requestMethod);
    }

}
