package com.oneliang.android.common.database;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.oneliang.frame.bean.Page;
import com.oneliang.frame.jdbc.BaseQuery.ExecuteType;
import com.oneliang.frame.jdbc.QueryException;

import android.database.Cursor;

public abstract interface Query {

	/**
	 * <p>Method: delete object</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObject(T object) throws QueryException;
	
	/**
	 * <p>Method: delete object,by condition</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObject(T object,String condition) throws QueryException;
	
	/**
	 * <p>Method: delete object,by table condition</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObject(T object,String table,String condition) throws QueryException;

    /**
	 * <p>Method: delete object not by id,it is sql binding</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObjectNotById(T object) throws QueryException;

	/**
	 * <p>Method: delete object not by id,by condition,sql binding</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObjectNotById(T object,String condition) throws QueryException;

	/**
	 * <p>Method: delete object not by id,by table condition,sql binding</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObjectNotById(T object,String table,String condition) throws QueryException;

	/**
     * <p>Method: delete class</p>
     * @param clazz
     * @throws QueryException
     */
    public abstract <T extends Object> void deleteObject(Class<T> clazz) throws QueryException;

	/**
	 * <p>Method: delete class,by condition</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObject(Class<T> clazz,String condition) throws QueryException;

	/**
	 * <p>Method: delete object collection,transaction,not sql binding</p>
	 * @param <T>
	 * @param collection
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObject(Collection<T> collection) throws QueryException;

	/**
	 * <p>Method: delete object collection,transaction,not sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T> void deleteObject(Collection<T> collection,String table) throws QueryException;

	/**
	 * <p>Method: delete object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void deleteObject(Collection<T> collection,Class<M> clazz) throws QueryException;

	/**
	 * <p>Method: delete object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void deleteObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException;

	/**
	 * <p>Method: delete object by id,not sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObjectById(Class<T> clazz,Serializable id) throws QueryException;

	/**
	 * <p>Method: delete object by multiple id,transaction,not sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param ids
	 * @throws QueryException
	 */
	public abstract <T extends Object> void deleteObjectByIds(Class<T> clazz,Serializable[] ids) throws QueryException;

	/**
	 * <p>Method: insert object</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public abstract <T extends Object> void insertObject(T object) throws QueryException;

	/**
	 * <p>Method: insert object</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object> void insertObject(T object,String table) throws QueryException;
	
	/**
	 * <p>Method: insert object collection,transaction</p>
	 * @param <T>
	 * @param collection
	 * @throws QueryException
	 */
	public abstract <T extends Object> void insertObject(Collection<T> collection) throws QueryException;
	
	/**
	 * <p>Method: insert object collection,transaction,not for sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object> void insertObject(Collection<T> collection,String table) throws QueryException;

	/**
	 * <p>Method: insert object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void insertObject(Collection<T> collection,Class<M> clazz) throws QueryException;

	/**
	 * <p>Method: insert object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void insertObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException;

	/**
	 * <p>Method: update object</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObject(T object) throws QueryException;
	
	/**
	 * <p>Method: update object,by condition</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObject(T object,String condition) throws QueryException;

	/**
	 * <p>Method: update object,by table,condition</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObject(T object,String table,String condition) throws QueryException;

	/**
	 * <p>Method: update object not by id,it is sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObjectNotById(T object) throws QueryException;

	/**
	 * <p>Method: update object not by id,sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObjectNotById(T object,String condition) throws QueryException;

	/**
	 * <p>Method: update object not by id,by table,condition,sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObjectNotById(T object,String table,String condition) throws QueryException;

	/**
	 * <p>Method: update object collection,transaction,not for sql binding</p>
	 * @param <T>
	 * @param collection
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObject(Collection<T> collection) throws QueryException;
	
	/**
	 * <p>Method: update object collection,transaction,not for sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object> void updateObject(Collection<T> collection,String table) throws QueryException;

	/**
	 * <p>Method: update object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void updateObject(Collection<T> collection,Class<M> clazz) throws QueryException;

	/**
	 * <p>Method: update object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @param table
	 * @throws QueryException
	 */
	public abstract <T extends Object,M extends Object> void updateObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException;

	/**
	 * <p>Method: select object by id</p>
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> T selectObjectById(Class<T> clazz, Serializable id) throws QueryException;
	
	/**
	 * <p>Through the class to find all</p>
	 * <p>Method: select object list</p>
	 * @param <T>
	 * @return List<T>
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz) throws QueryException;
	
	/**
	 * <p>Through the class to find all but with the condition</p>
	 * <p>Method: select object list,by condition</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String condition) throws QueryException;

	/**
	 * <p>Method: select object list,by condition,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method: select object list,by table,condition</p>
	 * @param <T>
	 * @param clazz
	 * @param table
	 * @param condition
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String table,String condition) throws QueryException;

	/**
	 * <p>Method: select object list,by table,condition,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String table,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method: select object list,by column,table,condition</p>
	 * @param <T>
	 * @param clazz
	 * @param selectColumns
	 * @param table
	 * @param condition
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String[] selectColumns,String table,String condition) throws QueryException;

	/**
	 * <p>Method: select object list,by column,table,condition,parameters,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param selectColumns
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectList(Class<T> clazz,String[] selectColumns,String table,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method: select object list by sql</p>
	 * @param <T>
	 * @param clazz
	 * @param sql
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectListBySql(Class<T> clazz,String sql) throws QueryException;

	/**
	 * <p>Method: select object list by sql,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param sql
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectListBySql(Class<T> clazz, String sql, String[] parameters) throws QueryException;

	/**
	 * <p>Method: select object pagination list,has implement</p>
	 * @param <T>
	 * @param clazz
	 * @param page
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz,Page page) throws QueryException;
	
	/**
	 * <p>Method: select object pagination list,has implement</p>
	 * @param <T>
	 * @param clazz
	 * @param page
	 * @param condition
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz,Page page,String condition) throws QueryException;
	
	/**
	 * <p>Method: select object pagination list,has implement,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param page
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz,Page page,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method: select object pagination list,has implement</p>
	 * @param <T>
	 * @param clazz
	 * @param page
	 * @param columns
	 * @param table
	 * @param condition
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz,Page page,String[] selectColumns,String table,String condition) throws QueryException;

	/**
	 * <p>Method: select object pagination list,has implement,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param page
	 * @param selectColumns
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public abstract <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz, Page page, String[] selectColumns, String table, String condition, String[] parameters) throws QueryException;

	/**
	 * <p>Method: execute all sql</p>
	 * @param sql
	 * @throws QueryException 
	 */
	public abstract void executeBySql(String sql) throws QueryException;

	/**
	 * <p>Method: execute all sql</p>
	 * @param sql
	 * @param parameters
	 * @throws QueryException
	 */
	public abstract void executeBySql(String sql,String[] parameters) throws QueryException;

	/**
     * <p>Method: execute query base on the sql command</p>
     * @param sql
     * @return Cursor
     * @throws QueryException
     */
    public abstract Cursor executeQueryBySql(String sql) throws QueryException;
    
    /**
     * <p>Method: execute query base on the sql command</p>
     * @param sql
     * @param parameters
     * @return Cursor
     * @throws QueryException
     */
    public abstract Cursor executeQueryBySql(String sql,String[] parameters) throws QueryException;

	/**
	 * <p>Method: execute update</p>
	 * @param object
	 * @param table
	 * @param executeType
	 * @throws QueryException
	 */
	public abstract <T extends Object> void executeUpdate(T object,String table,String condition,ExecuteType executeType) throws QueryException;

	/**
	 * <p>Method: execute batch</p>
	 * @param sqls
	 * @throws QueryException
	 */
	public abstract void executeBatch(String[] sqls) throws QueryException;

	/**
	 * <p>Method: table total rows</p>
	 * @param table
	 * @return int
	 * @throws QueryException
	 */
	public abstract int totalRows(String table) throws QueryException;

	/**
	 * <p>count table total rows</p>
	 * @param table
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public abstract int totalRows(String table,String condition) throws QueryException;

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(String table,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(Class<T> clazz) throws QueryException;

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(Class<T> clazz,String condition) throws QueryException;

	/**
	 * <p>Method; get the total size</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @param parameters
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(Class<T> clazz,String condition,String[] parameters) throws QueryException;

	/**
	 * <p>Method; get the total size</p>
	 * @param <T>
	 * @param clazz
	 * @param table
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(Class<T> clazz,String table,String condition) throws QueryException;

	/**
	 * <p>Method; get the total size</p>
	 * @param <T>
	 * @param clazz
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return int
	 * @throws QueryException
	 */
	public abstract <T extends Object> int totalRows(Class<T> clazz,String table,String condition,String[] parameters) throws QueryException;
}
