package com.oneliang.android.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.oneliang.Constant;
import com.oneliang.util.file.FileUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

public final class ApkUtil {

	private ApkUtil(){};

	/**
	 * install apk from asset
	 * 
	 * @param context
	 * @param externalFilePath
	 * @param assetApkFullFilename
	 * @return boolean
	 */
	public static boolean installApkFromAsset(Context context,String externalFilePath,String assetApkFullFilename) {
		boolean result=false;
		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open(assetApkFullFilename);
			if (inputStream != null) {
				FileUtil.createDirectory(externalFilePath);
				String apkPath = externalFilePath+assetApkFullFilename;
				File file = new File(apkPath);
				if (writeStreamToFile(inputStream, file)) {
					installApk(context, apkPath);
					result=true;
				}
			}
		} catch (IOException e) {
			Log.e(Constant.Base.EXCEPTION,e.getMessage());
		}
		return result;
	}

	/**
	 * download from web
	 * @param context
	 * @param url
	 */
	public static void openDownloadWeb(Context context, String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}

	/**
	 * write stream to file
	 * @param stream
	 * @param file
	 * @return boolean
	 */
	private static boolean writeStreamToFile(InputStream inputStream, File file) {
		boolean result=false;
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			final byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
			int length=-1;
			while ((length = inputStream.read(buffer,0,buffer.length)) != -1) {
				output.write(buffer, 0, length);
				output.flush();
			}
		} catch (Exception e) {
			Log.e(Constant.Base.EXCEPTION,e.getMessage());
		} finally {
			try {
				output.close();
				inputStream.close();
				result=true;
			} catch (IOException e) {
				Log.e(Constant.Base.EXCEPTION,e.getMessage());
			}
		}
		return result;
	}

	/**
	 * install apk
	 * @param context
	 * @param apkPath
	 */
	private static void installApk(Context context, String apkPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(apkPath)), Constant.Http.ContentType.APPLICATION_ANDROID_PACKAGE);
		context.startActivity(intent);
	}
}