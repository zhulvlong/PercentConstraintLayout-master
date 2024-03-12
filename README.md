### PercentConstraintLayout
[自定义Layout，让子View支持圆角属性](https://www.jianshu.com/p/afc930586210)


```
dependencies {
	        implementation 'com.github.zhulvlong:PercentConstraintLayout-master:3.0.3'
	}
```
for AndroidX
在gradle.properties中添加
```
android.useAndroidX=true
android.enableJetifier=true
```

```
<?xml version="1.0" encoding="utf-8"?>
<com.github.layout.PercentConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/pcl_running_count_down"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white"
    android:fitsSystemWindows="true">

    <TextView
        android:id="@+id/tv_running_count_down"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="3"
        android:textColor="#333333"
        android:textSize="134sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:percent_constraint_marginTop="552/1600" />
    
</com.github.layout.PercentConstraintLayout>
```
