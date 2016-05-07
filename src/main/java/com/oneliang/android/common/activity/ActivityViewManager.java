package com.oneliang.android.common.activity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ActivityViewManager {

	private Map<String,String> activityViewClassMap=new ConcurrentHashMap<String,String>();

	public void putActivityViewClass(String activityViewId,String activityViewClassName){
		this.activityViewClassMap.put(activityViewId, activityViewClassName);
	}

	public String getActivityViewClass(String activityViewId){
		return this.activityViewClassMap.get(activityViewId);
	}
}
