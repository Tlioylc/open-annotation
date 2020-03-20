package com.tlioylc.openannotation;


import com.google.auto.service.AutoService;
import com.tlioylc.openannotation.annotation.OpenBuilder;
import com.tlioylc.openannotation.annotation.Optional;
import com.tlioylc.openannotation.annotation.Require;
import com.tlioylc.openannotation.builders.LifecycleCallback;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/126:43 PM
 * desc   :
 */
@AutoService(Processor.class)
public class IntentProcessor extends AbstractProcessor {

    //文件相关辅助类
    private Filer mFiler;
    //日志信息
    private Messager messager;

    /**
     * 初始化工作，我们可以得到一些有用的工具，例如 Filer，我们需要它将生成的代码写入文件中
     *
     * @param env
     */
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFiler = env.getFiler();
        messager = env.getMessager();//获取信息打印工具
    }

    /**
     * 最重要的方法，所有的注解处理都是在此完成
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(OpenBuilder.class);

        if (elements == null || elements.isEmpty()) {
            return true;
        }
        LifecycleCallback.analysisCallBackInline(elements,mFiler);
        return false;
    }


    /**
     * 返回我们所要处理的注解的一个集合
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> arg = new HashSet<>();
        arg.add(OpenBuilder.class.getCanonicalName());
        arg.add(Optional.class.getCanonicalName());
        arg.add(Require.class.getCanonicalName());
        return arg;
    }

    /**
     * 要支持的java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
