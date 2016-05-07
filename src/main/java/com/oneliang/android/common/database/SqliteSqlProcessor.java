package com.oneliang.android.common.database;

import java.util.List;

import com.oneliang.frame.jdbc.DefaultSqlProcessor;
import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.ClassUtil.ClassType;
import com.oneliang.util.common.TimeUtil;

import android.database.Cursor;

public class SqliteSqlProcessor extends DefaultSqlProcessor implements SqlProcessor {

    /**
     * after select process,for result set to object
     * @param parameterType
     * @param cursor
     * @param columnName
     * @return Object
     */
    public Object afterSelectProcess(Class<?> parameterType, Cursor cursor, String columnName){
        final int columnIndex=cursor.getColumnIndex(columnName);
        Object value=null;
        ClassType classType=ClassUtil.getClassType(parameterType);
        switch(classType){
        case CHAR:
            value = cursor.getString(columnIndex).toCharArray()[0];
            break;
        case JAVA_LANG_STRING:
            value = cursor.getString(columnIndex);
            break;
        case JAVA_LANG_CHARACTER:
            value = Character.valueOf(cursor.getString(columnIndex).toCharArray()[0]);
            break;
        case BYTE:
            value = Byte.parseByte(cursor.getString(columnIndex));
            break;
        case JAVA_LANG_BYTE:
            value = Byte.valueOf(cursor.getString(columnIndex));
            break;
        case SHORT:
            value = cursor.getShort(columnIndex);
            break;
        case JAVA_LANG_SHORT:
            value = Short.valueOf(cursor.getShort(columnIndex));
            break;
        case INT:
            value = cursor.getInt(columnIndex);
            break;
        case JAVA_LANG_INTEGER:
            value = Integer.valueOf(cursor.getInt(columnIndex));
            break;
        case LONG:
            value = cursor.getLong(columnIndex);
            break;
        case JAVA_LANG_LONG:
            value = Long.valueOf(cursor.getLong(columnIndex));
            break;
        case FLOAT:
            value = cursor.getFloat(columnIndex);
            break;
        case JAVA_LANG_FLOAT:
            value = Float.valueOf(cursor.getFloat(columnIndex));
            break;
        case DOUBLE:
            value = cursor.getDouble(columnIndex);
            break;
        case JAVA_LANG_DOUBLE:
            value = Double.valueOf(cursor.getDouble(columnIndex));
            break;
        case JAVA_UTIL_DATE:
            String columnValue=cursor.getString(columnIndex);
            if(columnValue!=null){
                value = TimeUtil.stringToDate(cursor.getString(columnIndex), TimeUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
            }
            break;
        }
        return value;
    }

    /**
	 * parameter list to string array
	 * @param parameterList
	 * @return String[]
	 */
    public String[] parameterListToStringArray(List<Object> parameterList){
        String[] parameterArray=null;
        if(parameterList!=null){
            parameterArray=new String[parameterList.size()];
            int index=0;
            for(Object value:parameterList){
                if(value!=null){
                    Class<?> clazz=value.getClass();
                    if (clazz.equals(java.util.Date.class)) {
                        parameterArray[index++]=TimeUtil.dateToString((java.util.Date)value);
                    }else{
                        parameterArray[index++]=value.toString();
                    }
                }else{
                    parameterArray[index++]=null;
                }
            }
        }
        return parameterArray;
    }
}
