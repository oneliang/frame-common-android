package com.oneliang.android.common.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public final class ImageUtil
{
	private ImageUtil(){}

	/**
	 * input stream to bitmap
	 * @param inputStream
	 * @return Bitmap
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream){
		Bitmap bitmap=null;
		if(inputStream!=null){
			bitmap=BitmapFactory.decodeStream(inputStream);
		}
		return bitmap;
	}

	/**
	 * input stream to drawable 
	 * @param inputStream
	 * @return Drawable
	 */
	public static Drawable inputStreamToDrawable(InputStream inputStream){
		Bitmap bitmap=inputStreamToBitmap(inputStream);
		Drawable drawable=bitmapToDrawable(bitmap);
		return drawable;
	}

	/**
	 * bitmap to drawable
	 * @param bitmap
	 * @return Drawable
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap){
		BitmapDrawable bitmapDrawable=null;
		if(bitmap!=null){
			bitmapDrawable= new BitmapDrawable(bitmap);
		}
		return bitmapDrawable;
	}

	/**
	 * drawable to bitmap
	 * @param drawable
	 * @return Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable){
	    Bitmap bitmap=null;
	    if(drawable!=null&&(drawable instanceof BitmapDrawable)){
	        bitmap=((BitmapDrawable)drawable).getBitmap();
	    }
		return bitmap;
	}
}
