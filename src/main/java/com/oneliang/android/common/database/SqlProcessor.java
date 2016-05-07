package com.oneliang.android.common.database;

import java.util.List;

import android.database.Cursor;

public interface SqlProcessor extends com.oneliang.frame.jdbc.SqlUtil.SqlProcessor {

	/**
	 * after select process,for result set to object
	 * @param parameterType
	 * @param cursor
	 * @param columnName
	 * @return Object
	 */
	public abstract Object afterSelectProcess(Class<?> parameterType,Cursor cursor,String columnName);

	/**
	 * parameter list to string array
	 * @param parameterList
	 * @return String[]
	 */
	public abstract String[] parameterListToStringArray(List<Object> parameterList);
}
