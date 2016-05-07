package com.oneliang.android.common.loader.resources;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oneliang.frame.reflect.ReflectClass;
import com.oneliang.frame.reflect.ReflectException;
import com.oneliang.frame.reflect.ReflectField;
import com.oneliang.frame.reflect.ReflectMethod;
import com.oneliang.frame.reflect.ThrowableChain;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;

public final class AndroidReflect {

	private static final String TAG = "AndroidReflect";

	private static final String SUFFIX_REFLECT = "Reflect_";
	// reflect class
	public final static ReflectClass<Object> Reflect_LoadedApk;
	public final static ReflectClass<Object> Reflect_ActivityThread;
	public final static ReflectClass<AssetManager> Reflect_AssetManager;
	public final static ReflectClass<Object> Reflect_ContextImpl;
	public final static ReflectClass<ContextThemeWrapper> Reflect_ContextThemeWrapper;
	public final static ReflectClass<ContextWrapper> Reflect_ContextWrapper;
	public final static ReflectClass<Instrumentation> Reflect_Instrumentation;

	// reflect field
	public final static ReflectField<Object, Instrumentation> Reflect_ActivityThread_mInstrumentation;
	public final static ReflectField<Object, Map<String, Object>> Reflect_ActivityThread_mPackages;
	public final static ReflectField<Object, Resources> Reflect_LoadedApk_mResources;
	public final static ReflectField<Object, Resources> Reflect_ContextImpl_mResources;
	public final static ReflectField<Object, Resources.Theme> Reflect_ContextImpl_mTheme;
	public final static ReflectField<ContextThemeWrapper, Context> Reflect_ContextThemeWrapper_mBase;
	public final static ReflectField<ContextThemeWrapper, Resources> Reflect_ContextThemeWrapper_mResources;
	public final static ReflectField<ContextWrapper, Context> Reflect_ContextWrapper_mBase;

	// reflect method
	public final static ReflectMethod<Object, Object> Reflect_ActivityThread_currentActivityThread;
	public final static ReflectMethod<AssetManager, Object> Reflect_AssetManager_addAssetPath;

	// original value
	public static Application application = null;
	public static Resources proxyResources = null;

	// reflect value
	private static Object loadedApk = null;
	private static Object currentActivityThread = null;

	static{
		Reflect_LoadedApk = new ReflectClass<Object>("android.app.LoadedApk");
		Reflect_ActivityThread = new ReflectClass<Object>("android.app.ActivityThread");
		Reflect_AssetManager = new ReflectClass<AssetManager>(AssetManager.class);
		Reflect_ContextImpl = new ReflectClass<Object>("android.app.ContextImpl");
		Reflect_ContextThemeWrapper = new ReflectClass<ContextThemeWrapper>(ContextThemeWrapper.class);
		Reflect_ContextWrapper = new ReflectClass<ContextWrapper>("android.content.ContextWrapper");
		Reflect_Instrumentation = new ReflectClass<Instrumentation>("android.app.Instrumentation");
		Reflect_ActivityThread_mInstrumentation = Reflect_ActivityThread.getDeclaredField("mInstrumentation");
		Reflect_ActivityThread_mPackages = Reflect_ActivityThread.getDeclaredField("mPackages");
		Reflect_LoadedApk_mResources = Reflect_LoadedApk.getDeclaredField("mResources");
		Reflect_ContextImpl_mResources = Reflect_ContextImpl.getDeclaredField("mResources");
		Reflect_ContextImpl_mTheme = Reflect_ContextImpl.getDeclaredField("mTheme");
		Reflect_ContextThemeWrapper_mBase = Reflect_ContextThemeWrapper.getDeclaredField("mBase");
		Reflect_ContextThemeWrapper_mResources = Reflect_ContextThemeWrapper.getDeclaredField("mResources");
		Reflect_ContextWrapper_mBase = Reflect_ContextWrapper.getDeclaredField("mBase");
		Reflect_ActivityThread_currentActivityThread = Reflect_ActivityThread.getDeclaredMethod("currentActivityThread", new Class[] {});
		Reflect_AssetManager_addAssetPath = Reflect_AssetManager.getDeclaredMethod("addAssetPath", new Class[] { String.class });
		//
		try {
			initializeThrowableChainList();
		} catch (ReflectException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private AndroidReflect() {
	}

	/**
	 * initialize throwable chain list
	 * 
	 * @throws ReflectException
	 */
	private static void initializeThrowableChainList() throws ReflectException {
		Field[] fieldArray = AndroidReflect.class.getDeclaredFields();
		if (fieldArray != null) {
			try {
				List<ThrowableChain> throwableChainList = new ArrayList<ThrowableChain>();
				List<Throwable> throwableList = new ArrayList<Throwable>();
				for (Field field : fieldArray) {
					if (field.getName().startsWith(SUFFIX_REFLECT)) {
						field.setAccessible(true);
						Object value = field.get(AndroidReflect.class);
						if (value != null && value instanceof ThrowableChain) {
							ThrowableChain throwableChain=(ThrowableChain)value;
							throwableChainList.add(throwableChain);
							throwableList.addAll(throwableChain.getThrowableList());
						}
					}
				}
				System.err.println("api level:"+Build.VERSION.SDK_INT);
				System.err.println("throwable chain list:"+throwableChainList.size());
				System.err.println("throwable list:"+throwableList.size());
				for(Throwable throwable:throwableList){
					throwable.printStackTrace();
				}
			} catch (Exception e) {
				throw new ReflectException(e);
			}
		}
	}

	private static class ActivityThreadGetter implements Runnable {
		public void run() {
			try {
				currentActivityThread = Reflect_ActivityThread_currentActivityThread.invoke(Reflect_ActivityThread.getClazz(), new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (Reflect_ActivityThread_currentActivityThread) {
				Reflect_ActivityThread_currentActivityThread.notify();
			}
		}
	}

	public static Object getActivityThread() throws ReflectException {
		if (currentActivityThread == null) {
			if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
				currentActivityThread = Reflect_ActivityThread_currentActivityThread.invoke(null, new Object[0]);
			} else {
				Handler handler = new Handler(Looper.getMainLooper());
				synchronized (Reflect_ActivityThread_currentActivityThread) {
					handler.post(new ActivityThreadGetter());
					try {
						Reflect_ActivityThread_currentActivityThread.wait();
					} catch (InterruptedException e) {
						throw new ReflectException(e);
					}
				}
			}
		}
		return currentActivityThread;
	}

	@SuppressWarnings("unchecked")
	private static Object getLoadedApk(Object object, String packageName) throws ReflectException {
		if (loadedApk == null) {
			WeakReference<Object> weakReference = (WeakReference<Object>) ((Map<String, Object>) Reflect_ActivityThread_mPackages.get(object)).get(packageName);
			if (weakReference != null) {
				loadedApk = weakReference.get();
			}
		}
		return loadedApk;
	}

	/**
	 * get instrumentation
	 * 
	 * @return Instrumentation
	 * @throws Exception
	 */
	public static Instrumentation getInstrumentation() throws ReflectException {
		Object activityThread = getActivityThread();
		if (activityThread != null) {
			return Reflect_ActivityThread_mInstrumentation.get(activityThread);
		}
		throw new ReflectException("Failed to get ActivityThread.sCurrentActivityThread");
	}

	/**
	 * replace resources
	 * 
	 * @param application
	 * @param resources
	 * @param resourceFullFilenameList
	 * @param resourcesAdapter
	 * @throws Exception
	 */
	public static void replaceResources(Application application, Resources resources, List<String> resourceFullFilenameList, ResourcesAdapter resourcesAdapter) throws Exception {
		if (resourceFullFilenameList != null && !resourceFullFilenameList.isEmpty()) {
			AssetManager assetManager = AssetManager.class.newInstance();
			for (String resourceFullFilename : resourceFullFilenameList) {
				AndroidReflect.Reflect_AssetManager_addAssetPath.invoke(assetManager, new String[] { resourceFullFilename });
			}
			if (resourcesAdapter == null) {
				throw new NullPointerException("resourcesAdapter can not be null");
			}
			Resources proxyResources = resourcesAdapter.findSuitableProxyResources(assetManager, resources);
			AndroidReflect.proxyResources = proxyResources;
			AndroidReflect.replaceResources(application, proxyResources);
		}
	}

	/**
	 * replace resources
	 * 
	 * @param application
	 * @param resources
	 * @throws ReflectException
	 */
	public static void replaceResources(Application application, Resources resources) throws ReflectException {
		Object activityThread = getActivityThread();
		if (activityThread == null) {
			throw new ReflectException("Failed to get ActivityThread.sCurrentActivityThread");
		}
		Object loadedApk = getLoadedApk(activityThread, application.getPackageName());
		if (loadedApk == null) {
			throw new ReflectException("Failed to get ActivityThread.mLoadedApk");
		}
		Reflect_LoadedApk_mResources.set(loadedApk, resources);
		Reflect_ContextImpl_mResources.set(application.getBaseContext(), resources);
		Reflect_ContextImpl_mTheme.set(application.getBaseContext(), null);
	}

	/**
	 * replace instrumentation
	 * 
	 * @param instrumentation
	 * @throws ReflectException
	 */
	public static void replaceInstrumentation(Instrumentation instrumentation) throws ReflectException {
		Object activityThread = getActivityThread();
		if (activityThread == null) {
			throw new ReflectException("Failed to get ActivityThread.sCurrentActivityThread");
		}
		Reflect_ActivityThread_mInstrumentation.set(activityThread, instrumentation);
	}
}
