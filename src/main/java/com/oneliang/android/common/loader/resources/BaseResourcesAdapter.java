package com.oneliang.android.common.loader.resources;

import java.lang.reflect.Constructor;


import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class BaseResourcesAdapter implements ResourcesAdapter {

	/**
	 * find suitable proxy resources
	 * @param assetManager
	 * @param resources
	 * @return Resources
	 */
	public Resources findSuitableProxyResources(AssetManager assetManager, Resources resources) {
		Resources suitableResources = resources;
		if (resources == null || !resources.getClass().getName().equals("android.content.res.MiuiResources")) {
			suitableResources = new ProxyResources(assetManager, resources);
		} else {
			// mi ui
			try {
				Constructor<?> declaredConstructor = Class.forName("android.content.res.MiuiResources").getDeclaredConstructor(new Class[] { AssetManager.class, DisplayMetrics.class, Configuration.class });
				declaredConstructor.setAccessible(true);
				suitableResources = (Resources) declaredConstructor.newInstance(new Object[] { assetManager, resources.getDisplayMetrics(), resources.getConfiguration() });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return suitableResources;
	}
}
