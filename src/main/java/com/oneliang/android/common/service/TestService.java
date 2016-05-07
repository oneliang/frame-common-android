package com.oneliang.android.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {

	public IBinder onBind(Intent arg0) {
		System.out.println("testService.onBind:"+this);
		return null;
	}

	public void onCreate() {
		System.out.println("testService.onCreate:"+this);
	    super.onCreate();
	}
}
