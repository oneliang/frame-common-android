package com.oneliang.android.common.test.multidex;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.oneliang.frame.reflect.ReflectField;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by oneliang on 2017/11/23.
 */

public class SampleApplication extends Application {
    private static final String TAG = "sample";
    private static final String CONSTANT_M_BASE = "mBase";
    private static final String CONSTANT_M_PACKAGE_INFO = "mPackageInfo";
    private static final String CONSTANT_M_CLASSLOADER = "mClassLoader";
    private ProxyClassLoader selfClassLoader = null;

    public void onCreate() {
        super.onCreate();
        try {
            loadAllDex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test();
    }

    /**
     * load all dex
     * 
     * @throws Exception
     */
    private void loadAllDex() throws Exception {
        android.util.Log.i(TAG, "loadAllDex");
        ReflectField<Context, Context> applicationMBaseReflectField = new ReflectField<>(ContextWrapper.class, CONSTANT_M_BASE, 0);
        Context mBaseValue = applicationMBaseReflectField.get(this.getApplicationContext());
        android.util.Log.i(TAG, "mBaseValue:" + mBaseValue);
        ReflectField<Context, Object> mPackageInfoReflectField = new ReflectField<>(mBaseValue.getClass(), CONSTANT_M_PACKAGE_INFO, 0);
        Object mPackageInfoValue = mPackageInfoReflectField.get(mBaseValue);
        android.util.Log.i(TAG, "mPackageInfoValue:" + mPackageInfoValue);
        ReflectField<Object, ClassLoader> mClassLoaderInfoReflectField = new ReflectField<>(mPackageInfoValue.getClass(), CONSTANT_M_CLASSLOADER, 0);
        ClassLoader systemClassLoader = mClassLoaderInfoReflectField.get(mPackageInfoValue);
        android.util.Log.i(TAG, "systemClassLoader:" + systemClassLoader);
        this.selfClassLoader = new ProxyClassLoader(systemClassLoader.getParent());
        this.selfClassLoader.setLastClassLoader(systemClassLoader);
        mClassLoaderInfoReflectField.set(mPackageInfoValue, selfClassLoader);
    }

    /**
     * test
     */
    private void test() {
        String dexPath = "/sdcard/classes2.dex";

        // 指定dexoutputpath为APP自己的缓存目录
        File dexOutputDir = this.getDir("dex", 0);
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, this.selfClassLoader.getLastClassLoader());
        this.selfClassLoader.setLastClassLoader(dexClassLoader);
        ClassLoader parent = this.selfClassLoader.getLastClassLoader();
        while (parent != null) {
            android.util.Log.i(TAG, "class loader:" + parent.toString());
            parent = parent.getParent();
        }
        try {
            Class<?> clazz = this.selfClassLoader.loadClass("com.tencent.mm.toolkit.sample.SecondActivity");
            android.util.Log.i(TAG, "second activity class:" + (clazz == null ? null : clazz.toString()));
        } catch (Exception e) {
            android.util.Log.e(TAG, "", e);
        }
    }
}
