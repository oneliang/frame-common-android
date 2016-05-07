package com.oneliang.android.common.loader.resources;

import com.oneliang.frame.reflect.ReflectException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ProxyInstrumentation extends Instrumentation {

	private static final String TAG = "ProxyInstrumentation";

	private Context context;
	private Instrumentation instrumentation;

	private static interface ExecStartActivityCallback {
		ActivityResult execStartActivity();
	}

	public ProxyInstrumentation(Instrumentation instrumentation, Context context) {
		this.context = context;
		this.instrumentation = instrumentation;
	}

	public ActivityResult execStartActivity(final Context context, final IBinder iBinder, final IBinder iBinder2, final Activity activity, final Intent intent, final int i) {
		return execStartActivityInternal(this.context, intent, new ExecStartActivityCallback() {
			public ActivityResult execStartActivity() {
				try {
					return (ActivityResult) AndroidReflect.Reflect_Instrumentation.getDeclaredMethod("execStartActivity", new Class[] { Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class }).invoke(instrumentation,
							new Object[] { context, iBinder, iBinder2, activity, intent, i });
				} catch (Throwable ex) {
					ex.printStackTrace();
					return null;
				}

			}
		});
	}

	@TargetApi(16)
	public ActivityResult execStartActivity(final Context context, final IBinder iBinder, final IBinder iBinder2, final Activity activity, final Intent intent, final int i, final Bundle bundle) {
		return execStartActivityInternal(this.context, intent, new ExecStartActivityCallback() {
			@Override
			public ActivityResult execStartActivity() {
				try {
					Object result = AndroidReflect.Reflect_Instrumentation.getDeclaredMethod("execStartActivity", new Class[] { Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class }).invoke(instrumentation,
							new Object[] { context, iBinder, iBinder2, activity, intent, i, bundle });
					if (result != null)
						return (ActivityResult) result;
				} catch (Throwable ex) {
					ex.printStackTrace();

				}
				return null;
			}
		});
	}

	@TargetApi(14)
	public ActivityResult execStartActivity(final Context context, final IBinder iBinder, final IBinder iBinder2, final Fragment fragment, final Intent intent, final int i) {
		return execStartActivityInternal(this.context, intent, new ExecStartActivityCallback() {
			@Override
			public ActivityResult execStartActivity() {
				try {
					return (ActivityResult) AndroidReflect.Reflect_Instrumentation.getDeclaredMethod("execStartActivity", new Class[] { Context.class, IBinder.class, IBinder.class, Fragment.class, Intent.class, int.class }).invoke(instrumentation,
							new Object[] { context, iBinder, iBinder2, fragment, intent, i });
				} catch (Throwable ex) {
					ex.printStackTrace();
					return null;
				}

			}
		});
	}

	@TargetApi(16)
	public ActivityResult execStartActivity(final Context context, final IBinder iBinder, final IBinder iBinder2, final Fragment fragment, final Intent intent, final int i, final Bundle bundle) {
		return execStartActivityInternal(this.context, intent, new ExecStartActivityCallback() {
			@Override
			public ActivityResult execStartActivity() {
				try {
					return (ActivityResult) AndroidReflect.Reflect_Instrumentation.getDeclaredMethod("execStartActivity", new Class[] { Context.class, IBinder.class, IBinder.class, Fragment.class, Intent.class, int.class, Bundle.class }).invoke(
							instrumentation, new Object[] { context, iBinder, iBinder2, fragment, intent, i, bundle });
				} catch (Throwable ex) {
					ex.printStackTrace();
					return null;
				}
			}
		});
	}

	private ActivityResult execStartActivityInternal(Context context, Intent intent, ExecStartActivityCallback execStartActivityCallback) {
		String packageName;
		if (intent.getComponent() != null) {
			packageName = intent.getComponent().getPackageName();
		} else {
			ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
			if (resolveActivity == null || resolveActivity.activityInfo == null) {
				packageName = null;
			} else {
				packageName = resolveActivity.activityInfo.packageName;
			}
		}
		if (context.getPackageName() != null && !context.getPackageName().equals(packageName)) {
			return execStartActivityCallback.execStartActivity();
		}

		return execStartActivityCallback.execStartActivity();
	}

	public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj)
			throws InstantiationException, IllegalAccessException {
		Activity newActivity = this.instrumentation.newActivity(cls, context, iBinder, application, intent, activityInfo, charSequence, activity, str, obj);
		if (AndroidReflect.application.getPackageName().equals(activityInfo.packageName) && AndroidReflect.Reflect_ContextThemeWrapper_mResources != null && AndroidReflect.Reflect_ContextThemeWrapper_mResources.getField() != null) {
			try {
				AndroidReflect.Reflect_ContextThemeWrapper_mResources.set(newActivity, AndroidReflect.proxyResources);
			} catch (ReflectException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return newActivity;
	}

	public Activity newActivity(ClassLoader classLoader, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Activity newActivity = null;
		try {
			newActivity = this.instrumentation.newActivity(classLoader, className, intent);
			if (AndroidReflect.Reflect_ContextThemeWrapper_mResources != null && AndroidReflect.Reflect_ContextThemeWrapper_mResources.getField() != null) {
				AndroidReflect.Reflect_ContextThemeWrapper_mResources.set(newActivity, AndroidReflect.proxyResources);
			}
		} catch (ReflectException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
		return newActivity;
	}

	public void callActivityOnCreate(Activity activity, Bundle bundle) {
		if (AndroidReflect.application.getPackageName().equals(activity.getPackageName())) {
			ProxyContextWrapper proxyContextWrapper = new ProxyContextWrapper(activity.getBaseContext());
			try {
				if (AndroidReflect.Reflect_ContextThemeWrapper_mBase != null && AndroidReflect.Reflect_ContextThemeWrapper_mBase.getField() != null) {
					AndroidReflect.Reflect_ContextThemeWrapper_mBase.set(activity, proxyContextWrapper);
				}
				AndroidReflect.Reflect_ContextWrapper_mBase.set(activity, proxyContextWrapper);
			} catch (ReflectException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		this.instrumentation.callActivityOnCreate(activity, bundle);
	}

	@TargetApi(18)
	public UiAutomation getUiAutomation() {
		return this.instrumentation.getUiAutomation();
	}

	public void onCreate(Bundle bundle) {
		this.instrumentation.onCreate(bundle);
	}

	public void start() {
		this.instrumentation.start();
	}

	public void onStart() {
		this.instrumentation.onStart();
	}

	public boolean onException(Object obj, Throwable th) {
		return this.instrumentation.onException(obj, th);
	}

	public void sendStatus(int i, Bundle bundle) {
		this.instrumentation.sendStatus(i, bundle);
	}

	public void finish(int i, Bundle bundle) {
		this.instrumentation.finish(i, bundle);
	}

	public void setAutomaticPerformanceSnapshots() {
		this.instrumentation.setAutomaticPerformanceSnapshots();
	}

	public void startPerformanceSnapshot() {
		this.instrumentation.startPerformanceSnapshot();
	}

	public void endPerformanceSnapshot() {
		this.instrumentation.endPerformanceSnapshot();
	}

	public void onDestroy() {
		this.instrumentation.onDestroy();
	}

	public Context getContext() {
		return this.instrumentation.getContext();
	}

	public ComponentName getComponentName() {
		return this.instrumentation.getComponentName();
	}

	public Context getTargetContext() {
		return this.instrumentation.getTargetContext();
	}

	public boolean isProfiling() {
		return this.instrumentation.isProfiling();
	}

	public void startProfiling() {
		this.instrumentation.startProfiling();
	}

	public void stopProfiling() {
		this.instrumentation.stopProfiling();
	}

	public void setInTouchMode(boolean z) {
		this.instrumentation.setInTouchMode(z);
	}

	public void waitForIdle(Runnable runnable) {
		this.instrumentation.waitForIdle(runnable);
	}

	public void waitForIdleSync() {
		this.instrumentation.waitForIdleSync();
	}

	public void runOnMainSync(Runnable runnable) {
		this.instrumentation.runOnMainSync(runnable);
	}

	public Activity startActivitySync(Intent intent) {
		return this.instrumentation.startActivitySync(intent);
	}

	public void addMonitor(ActivityMonitor activityMonitor) {
		this.instrumentation.addMonitor(activityMonitor);
	}

	public ActivityMonitor addMonitor(IntentFilter intentFilter, ActivityResult activityResult, boolean z) {
		return this.instrumentation.addMonitor(intentFilter, activityResult, z);
	}

	public ActivityMonitor addMonitor(String str, ActivityResult activityResult, boolean z) {
		return this.instrumentation.addMonitor(str, activityResult, z);
	}

	public boolean checkMonitorHit(ActivityMonitor activityMonitor, int i) {
		return this.instrumentation.checkMonitorHit(activityMonitor, i);
	}

	public Activity waitForMonitor(ActivityMonitor activityMonitor) {
		return this.instrumentation.waitForMonitor(activityMonitor);
	}

	public Activity waitForMonitorWithTimeout(ActivityMonitor activityMonitor, long j) {
		return this.instrumentation.waitForMonitorWithTimeout(activityMonitor, j);
	}

	public void removeMonitor(ActivityMonitor activityMonitor) {
		this.instrumentation.removeMonitor(activityMonitor);
	}

	public boolean invokeMenuActionSync(Activity activity, int i, int i2) {
		return this.instrumentation.invokeMenuActionSync(activity, i, i2);
	}

	public boolean invokeContextMenuAction(Activity activity, int i, int i2) {
		return this.instrumentation.invokeContextMenuAction(activity, i, i2);
	}

	public void sendStringSync(String str) {
		this.instrumentation.sendStringSync(str);
	}

	public void sendKeySync(KeyEvent keyEvent) {
		this.instrumentation.sendKeySync(keyEvent);
	}

	public void sendKeyDownUpSync(int i) {
		this.instrumentation.sendKeyDownUpSync(i);
	}

	public void sendCharacterSync(int i) {
		this.instrumentation.sendCharacterSync(i);
	}

	public void sendPointerSync(MotionEvent motionEvent) {
		this.instrumentation.sendPointerSync(motionEvent);
	}

	public void sendTrackballEventSync(MotionEvent motionEvent) {
		this.instrumentation.sendTrackballEventSync(motionEvent);
	}

	public Application newApplication(ClassLoader classLoader, String str, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return this.instrumentation.newApplication(classLoader, str, context);
	}

	public void callApplicationOnCreate(Application application) {
		this.instrumentation.callApplicationOnCreate(application);
	}

	public void callActivityOnDestroy(Activity activity) {
		this.instrumentation.callActivityOnDestroy(activity);
	}

	public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle) {
		this.instrumentation.callActivityOnRestoreInstanceState(activity, bundle);
	}

	public void callActivityOnPostCreate(Activity activity, Bundle bundle) {
		this.instrumentation.callActivityOnPostCreate(activity, bundle);
	}

	public void callActivityOnNewIntent(Activity activity, Intent intent) {
		this.instrumentation.callActivityOnNewIntent(activity, intent);
	}

	public void callActivityOnStart(Activity activity) {
		this.instrumentation.callActivityOnStart(activity);
	}

	public void callActivityOnRestart(Activity activity) {
		this.instrumentation.callActivityOnRestart(activity);
	}

	public void callActivityOnResume(Activity activity) {
		this.instrumentation.callActivityOnResume(activity);
	}

	public void callActivityOnStop(Activity activity) {
		this.instrumentation.callActivityOnStop(activity);
	}

	public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle) {
		this.instrumentation.callActivityOnSaveInstanceState(activity, bundle);
	}

	public void callActivityOnPause(Activity activity) {
		this.instrumentation.callActivityOnPause(activity);
	}

	public void callActivityOnUserLeaving(Activity activity) {
		this.instrumentation.callActivityOnUserLeaving(activity);
	}

	public void startAllocCounting() {
		this.instrumentation.startAllocCounting();
	}

	public void stopAllocCounting() {
		this.instrumentation.stopAllocCounting();
	}

	public Bundle getAllocCounts() {
		return this.instrumentation.getAllocCounts();
	}

	public Bundle getBinderCounts() {
		return this.instrumentation.getBinderCounts();
	}
}
