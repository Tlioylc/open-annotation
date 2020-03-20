package com.tlioylc.annotationtest;

import android.app.Application;

import com.tlioylc.openannotation.OpenActivityLifecycleCallback;


/**
 * author : tlioylc
 * e-mail : tlioylc@gmail.com
 * date   : 2020/3/1312:25 PM
 * desc   :
 */
public class AnnApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new OpenActivityLifecycleCallback());
    }
}
