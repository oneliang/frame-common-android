package com.oneliang.android.common.loader.resources;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

public class ProxyContextWrapper extends ContextWrapper {

	private static final String TAG = "ProxyContextWrapper";

	public ProxyContextWrapper(Context context) {
		super(context);

	}

	public Resources getResources() {
		Log.i(TAG, "getResources is invoke");
		return AndroidReflect.proxyResources;
	}

	public AssetManager getAssets() {
		Log.i(TAG, "getAssets is invoke");
		return AndroidReflect.proxyResources.getAssets();
	}

}
