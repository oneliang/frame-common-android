package com.oneliang.android.common.loader.resources;

import android.content.res.AssetManager;
import android.content.res.Resources;

public class ProxyResources extends Resources {

	public ProxyResources(AssetManager assets, Resources resources) {
		super(assets, resources.getDisplayMetrics(), resources.getConfiguration());
	}
}
