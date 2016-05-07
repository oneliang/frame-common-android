package com.oneliang.android.common.loader.resources;

import android.content.res.AssetManager;
import android.content.res.Resources;

public abstract interface ResourcesAdapter {

	/**
	 * find suitable proxy resources
	 * @param assetManager
	 * @param resources
	 * @return Resources
	 */
	public abstract Resources findSuitableProxyResources(AssetManager assetManager, Resources resources);
}
