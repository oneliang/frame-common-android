package com.oneliang.android.common.resources.parse.block;

public class Logger {

	private Logger(){}

	public static void log(Object message){
		System.out.println("\t"+message.toString());
	}
}
