package com.oneliang.android.common.database;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.Constant;
import com.oneliang.exception.MappingNotFoundException;
import com.oneliang.frame.jdbc.MappingBean;
import com.oneliang.util.common.ObjectUtil;

import android.database.Cursor;

public final class SqliteUtil {

	private SqliteUtil() {
	}

	/**
	 * <p>
	 * Method: for sqlite database use,cursor to object list
	 * </p>
	 * 
	 * @param <T>
	 * @param cursor
	 * @param clazz
	 * @param mappingBean
	 * @return List<T>
	 */
	public static <T extends Object> List<T> cursorToObjectList(Cursor cursor, Class<T> clazz, MappingBean mappingBean, SqlProcessor sqlProcessor) {
		List<T> list = null;
		if (mappingBean != null && cursor != null) {
			try {
				list = new ArrayList<T>();
				T object = null;
				// Field[] field = clazz.getDeclaredFields();// get the fields
				// one
				Method[] methods = clazz.getMethods();
				// time is ok
				int rowCount = cursor.getCount();
				int index = 0;
				while (index < rowCount) {
					object = clazz.newInstance();// more instance
					cursor.moveToPosition(index);
					String columnName = null;
					for (Method method : methods) {
						String methodName = method.getName();
						String fieldName = null;
						if (methodName.startsWith(Constant.Method.PREFIX_SET)) {
							fieldName = ObjectUtil.methodNameToFieldName(Constant.Method.PREFIX_SET, methodName);
						}
						if (fieldName != null) {
							columnName = mappingBean.getColumn(fieldName);
							if (columnName != null) {
								Class<?>[] classes = method.getParameterTypes();
								Object value = null;
								if (classes.length == 1) {
									if (sqlProcessor != null) {
										value = sqlProcessor.afterSelectProcess(classes[0], cursor, columnName);
									}
									method.invoke(object, value);
								}
							}
						}
					}
					list.add(object);
					index++;
				}
			} catch (Exception e) {
				throw new SqliteUtilException(e);
			}
		} else {
			throw new MappingNotFoundException("Can not find the object mapping:"+clazz);
		}
		return list;
	}

	public static class SqliteUtilException extends RuntimeException{
		private static final long serialVersionUID = 4612763489216995632L;
		public SqliteUtilException(Throwable cause) {
			super(cause);
		}
	}
}
