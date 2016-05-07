package com.oneliang.android.common.util;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public final class AssetImageFileManager {

	private static final Map<String,Bitmap> bitmapMap=new ConcurrentHashMap<String, Bitmap>();
	
	private AssetImageFileManager(){}
	
	/**
	 * get bitmap from asset manager
	 * @param uri
	 * @param assetManager
	 * @return Bitmap
	 */
	public static Bitmap getBitmap(String uri,AssetManager assetManager){
		Bitmap bitmap=null;
		if(uri!=null){
			bitmap=bitmapMap.get(uri);
			if(bitmap==null&&assetManager!=null){
				try {
					InputStream inputStream=assetManager.open(uri);
					bitmap=BitmapFactory.decodeStream(inputStream);
					inputStream.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				if(bitmap!=null){
					bitmapMap.put(uri, bitmap);
				}
			}
		}
		return bitmap;
	}
}
