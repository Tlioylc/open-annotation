## 引入方式 ：
  <br>1、将open-annotation.jar放入项目libs文件夹
  <br>2、java项目app.gradle: 
```
  implementation files('libs/open-annotation.jar')
  annotationProcessor files('libs/open-annotation.jar')
```
   <br>kotlin项目app.gradle:
``` 
    annotationProcessor files('libs/open-annotation.jar')
    kapt files('libs/open-annotation.jar')
    implementation files('libs/open-annotation.jar')
```
<br>混淆代码 
```
 -keep class com.tlioylc.openannotation.** { *; }
-dontwarn com.tlioylc.openannotation.**
-keepclasseswithmembers class ** {
    @com.tlioylc.openannotation.** <fields>;
}
```
## 使用方式

@OpenBuilder来声明当前activity需要进行注解处理
```
@OpenBuilder
public class Main2Activity
```
@Require为必须参数
```
 @Require
    int targetValue1;
```
@Optional为非必须参数
```
 @Optional
    int targetValue2;
```
将自动生成 Main2ActivityBuilder类，使用方式
```
    Main2ActivityBuilder.init(1)
                    .setTargetValue2(1)
                    .open(this);
```    
另外需要在您的Application的onCreate方法中添加
```    
registerActivityLifecycleCallbacks(new OpenActivityLifecycleCallback()); 
如果已有registerActivityLifecycleCallbacks，可继承OpenActivityLifecycleCallback，并重写onActivityCreated
添加 super.onActivityCreated(activity,savedInstanceState);
 @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            super.onActivityCreated(activity,savedInstanceState);
        }
```    
