package com.oneliang.android.common.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.oneliang.Constants;
import com.oneliang.android.common.context.AndroidConfigurationFactory;
import com.oneliang.frame.ConfigurationFactory;
import com.oneliang.frame.bean.Page;
import com.oneliang.frame.jdbc.BaseQuery.ExecuteType;
import com.oneliang.frame.jdbc.DatabaseMappingUtil;
import com.oneliang.frame.jdbc.MappingBean;
import com.oneliang.frame.jdbc.QueryException;
import com.oneliang.frame.jdbc.SqlInjectUtil;
import com.oneliang.frame.jdbc.SqlUtil;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteQueryImpl implements Query {

    protected SQLiteDatabase sqliteDatabase=null;

    private static Logger logger=LoggerManager.getLogger(SqliteQueryImpl.class);

    private static final SqlProcessor SQLITE_SQL_PROCESSOR=new SqliteSqlProcessor();
    
    private SqlProcessor sqlProcessor=SQLITE_SQL_PROCESSOR;

    /**
     * constructor
     * @param databaseFilePath,absolute path
     */
    public SqliteQueryImpl(String databaseFilePath){
        if(databaseFilePath!=null){
            this.sqliteDatabase=SQLiteDatabase.openOrCreateDatabase(databaseFilePath, null);
        }else{
        	throw new NullPointerException("database file path can not be null");
        }
    }

    /**
     * <p>Method: delete object</p>
     * @param <T>
     * @param object
     * @throws QueryException
     */
    public <T extends Object> void deleteObject(T object) throws QueryException {
        this.deleteObject(object,null);
    }

    /**
     * <p>Method: delete object,by condition</p>
     * @param <T>
     * @param object
     * @param condition
     * @throws QueryException
     */
    public <T extends Object> void deleteObject(T object, String condition) throws QueryException {
        this.deleteObject(object,null,condition);
    }

    /**
     * <p>Method: delete object,by table condition</p>
     * @param <T>
     * @param object
     * @param table
     * @param condition
     * @throws QueryException
     */
    public <T extends Object> void deleteObject(T object, String table, String condition) throws QueryException {
        this.executeUpdate(object, table,condition,ExecuteType.DELETE_BY_ID);
    }

    /**
     * <p>Method: delete object not by id,it is sql binding</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObjectNotById(T object) throws QueryException{
		this.deleteObjectNotById(object, null);
	}

	/**
	 * <p>Method: delete object not by id,by condition,sql binding</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObjectNotById(T object,String condition) throws QueryException{
		this.deleteObjectNotById(object, null, condition);
	}

	/**
	 * <p>Method: delete object not by id,by table condition,sql binding</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObjectNotById(T object,String table,String condition) throws QueryException{
		this.executeUpdate(object, table,condition,ExecuteType.DELETE_NOT_BY_ID);
	}

    /**
     * <p>Method: delete class</p>
     * @param clazz
     * @throws QueryException
     */
    public <T extends Object> void deleteObject(Class<T> clazz) throws QueryException {
        this.deleteObject(clazz, null);
    }

    /**
     * <p>Method: delete class,by condition</p>
     * @param <T>
     * @param clazz
     * @param condition
     * @throws QueryException
     */
    public <T extends Object> void deleteObject(Class<T> clazz, String condition) throws QueryException {
        MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
        String sql=SqlUtil.deleteSql(null,condition, mappingBean);
        this.executeBySql(sql);
    }


	/**
	 * <p>Method: delete object collection,transaction,not sql binding</p>
	 * @param <T>
	 * @param collection
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObject(Collection<T> collection) throws QueryException{
		this.deleteObject(collection, StringUtil.BLANK);
	}

	/**
	 * <p>Method: delete object collection,transaction,not sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
	public <T> void deleteObject(Collection<T> collection,String table) throws QueryException{
		this.executeUpdate(collection, table, ExecuteType.DELETE_BY_ID);
	}

	/**
	 * <p>Method: delete object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void deleteObject(Collection<T> collection,Class<M> clazz) throws QueryException{
		this.deleteObject(collection, clazz, null);
	}

	/**
	 * <p>Method: delete object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz
	 * @param table
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void deleteObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException{
		this.executeUpdate(collection, clazz, table, ExecuteType.DELETE_BY_ID);
	}

    /**
	 * <p>Method: delete object by id</p>
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObjectById(Class<T> clazz, Serializable id) throws QueryException {
	    String sql=null;
	    try {
	        MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
	        sql = SqlUtil.classToDeleteOneRowSql(clazz, id,mappingBean);
	    } catch (Exception e) {
	        throw new QueryException(e);
	    }
	    this.executeBySql(sql);
	}

	/**
	 * <p>Method: delete object by multi id,transaction</p>
	 * @param <T>
	 * @param clazz
	 * @param ids
	 * @throws QueryException
	 */
	public <T extends Object> void deleteObjectByIds(Class<T> clazz, Serializable[] ids) throws QueryException {
	    String sql=null;
	    try {
	        MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
	        sql = SqlUtil.classToDeleteMultipleRowSql(clazz, ids,mappingBean);
	    } catch (Exception e) {
	        throw new QueryException(e);
	    }
	    this.executeBySql(sql);
	}

	/**
     * <p>Method: insert object</p>
     * @param <T>
     * @param object
     * @throws QueryException
     */
    public <T extends Object> void insertObject(T object) throws QueryException {
        this.insertObject(object,null);
    }

    /**
     * <p>Method: insert object</p>
     * @param <T>
     * @param object
     * @param table
     * @throws QueryException
     */
    public <T extends Object> void insertObject(T object, String table) throws QueryException {
        this.executeUpdate(object, table, null, ExecuteType.INSERT);
    }

    /**
     * <p>Method: insert object collection,transaction</p>
     * @param <T>
     * @param collection
     * @throws QueryException
     */
    public <T extends Object> void insertObject(Collection<T> collection) throws QueryException {
        this.insertObject(collection, StringUtil.BLANK);
    }

	/**
	 * <p>Method: insert object collection,transaction,not for sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
	public <T extends Object> void insertObject(Collection<T> collection,String table) throws QueryException{
		this.executeUpdate(collection, table, ExecuteType.INSERT);
	}

	/**
	 * <p>Method: insert object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void insertObject(Collection<T> collection,Class<M> clazz) throws QueryException{
		this.insertObject(collection, clazz, null);
	}

	/**
	 * <p>Method: insert object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @param table
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void insertObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException{
		this.executeUpdate(collection, clazz, table, ExecuteType.INSERT);
	}

    /**
     * <p>Method: update object</p>
     * @param <T>
     * @param object
     * @throws QueryException
     */
    public <T extends Object> void updateObject(T object) throws QueryException {
        this.updateObject(object, null);
    }

    /**
     * <p>Method: update object,by condition</p>
     * @param <T>
     * @param object
     * @param condition
     * @throws QueryException
     */
    public <T extends Object> void updateObject(T object, String condition) throws QueryException {
        this.updateObject(object, null, condition);
    }

    /**
     * <p>Method: update object,by table,condition</p>
     * @param <T>
     * @param object
     * @param table
     * @param condition
     * @throws QueryException
     */
    public <T extends Object> void updateObject(T object, String table, String condition) throws QueryException {
        this.executeUpdate(object, table, condition, ExecuteType.UPDATE_BY_ID);
    }

    /**
	 * <p>Method: update object not by id,it is sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @throws QueryException
	 */
	public <T extends Object> void updateObjectNotById(T object) throws QueryException{
		this.updateObjectNotById(object, null);
	}

	/**
	 * <p>Method: update object not by id,sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @param condition
	 * @throws QueryException
	 */
	public <T extends Object> void updateObjectNotById(T object,String condition) throws QueryException{
		this.updateObjectNotById(object, null, condition);
	}

	/**
	 * <p>Method: update object not by id,by table,condition,sql binding,null value field is not update</p>
	 * @param <T>
	 * @param object
	 * @param table
	 * @param condition
	 * @throws QueryException
	 */
	public <T extends Object> void updateObjectNotById(T object,String table,String condition) throws QueryException{
		this.executeUpdate(object, table, condition, ExecuteType.UPDATE_NOT_BY_ID);
	}

    /**
     * <p>Method: update object collection,transaction,not for sql binding</p>
     * @param <T>
     * @param collection
     * @throws QueryException
     */
    public <T extends Object> void updateObject(Collection<T> collection) throws QueryException {
    	this.updateObject(collection,StringUtil.BLANK);
    }

    /**
	 * <p>Method: update object collection,transaction,not for sql binding</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @throws QueryException
	 */
    public <T extends Object> void updateObject(Collection<T> collection,String table) throws QueryException{
    	this.executeUpdate(collection, table, ExecuteType.UPDATE_BY_ID);
    }

    /**
	 * <p>Method: update object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void updateObject(Collection<T> collection,Class<M> clazz) throws QueryException{
		this.updateObject(collection, clazz, null);
	}

	/**
	 * <p>Method: update object collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz mapping class
	 * @param table
	 * @throws QueryException
	 */
	public <T extends Object,M extends Object> void updateObject(Collection<T> collection,Class<M> clazz,String table) throws QueryException{
		this.executeUpdate(collection, clazz, table, ExecuteType.UPDATE_BY_ID);
	}

    /**
     * <p>Method: select object by id</p>
     * @param <T>
     * @param clazz
     * @param id
     * @throws QueryException
     */
    public <T extends Object> T selectObjectById(Class<T> clazz, Serializable id) throws QueryException {
        T object=null;
        List<T> list=null;
        Cursor cursor=null;
        try {
            MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
            String sql=SqlUtil.classToSelectIdSql(clazz,mappingBean);
            cursor=this.executeQueryBySql(sql,new String[]{id.toString()});
            list=SqliteUtil.cursorToObjectList(cursor,clazz,mappingBean,this.sqlProcessor);
        }catch(Exception e) {
            throw new QueryException(e);
        }finally{
            if(cursor!=null&&!cursor.isClosed()){
                cursor.close();
            }
        }
        if(list!=null&&!list.isEmpty()){
            object=list.get(0);
        }
        return object;
    }

    /**
     * <p>Through the class to find all</p>
     * <p>Method: select object list</p>
     * @param <T>
     * @return List<T>
     */
    public <T extends Object> List<T> selectObjectList(Class<T> clazz) throws QueryException {
        return this.selectObjectList(clazz,null);
    }

    /**
     * <p>Through the class to find all but with the condition</p>
     * <p>Method: select object list,by condition</p>
     * @param <T>
     * @param clazz
     * @param condition
     * @return List<T>
     * @throws QueryException
     */
    public <T extends Object> List<T> selectObjectList(Class<T> clazz,String condition) throws QueryException{
        return this.selectObjectList(clazz,null,condition);
    }

    /**
	 * <p>Method: select object list,by condition,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
    public <T extends Object> List<T> selectObjectList(Class<T> clazz, String condition, String[] parameters) throws QueryException {
    	return this.selectObjectList(clazz, null, condition, parameters);
    }

    /**
     * <p>Method: select object list,by table,condition</p>
     * @param <T>
     * @param clazz
     * @param table
     * @param condition
     * @return List<T>
     * @throws QueryException
     */
    public <T extends Object> List<T> selectObjectList(Class<T> clazz, String table, String condition) throws QueryException {
        return this.selectObjectList(clazz,null,table, condition);
    }

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
    public <T extends Object> List<T> selectObjectList(Class<T> clazz, String table, String condition, String[] parameters) throws QueryException {
    	return this.selectObjectList(clazz, null, table, condition, parameters);
    }

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
    public <T extends Object> List<T> selectObjectList(Class<T> clazz, String[] selectColumns, String table, String condition) throws QueryException {
        MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
        String sql=SqlUtil.selectSql(selectColumns,table,condition,mappingBean);
        Cursor cursor=null;
        List<T> list=null;
        try{
            cursor=this.executeQueryBySql(sql);
            list=SqliteUtil.cursorToObjectList(cursor, clazz, mappingBean, this.sqlProcessor);
        }catch(Exception e){
            throw new QueryException(e);
        }finally{
            if(cursor!=null&&!cursor.isClosed()){
                cursor.close();
            }
        }
        return list;
    }

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
	public <T extends Object> List<T> selectObjectList(Class<T> clazz,String[] selectColumns,String table,String condition,String[] parameters) throws QueryException{
		MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
        String sql=SqlUtil.selectSql(selectColumns,table,condition,mappingBean);
        Cursor cursor=null;
        List<T> list=null;
        try{
            cursor=this.executeQueryBySql(sql,parameters);
            list=SqliteUtil.cursorToObjectList(cursor, clazz, mappingBean, this.sqlProcessor);
        }catch(Exception e){
            throw new QueryException(e);
        }finally{
            if(cursor!=null&&!cursor.isClosed()){
                cursor.close();
            }
        }
		return list;
	}

    /**
     * <p>Method: select object list by sql</p>
     * @param <T>
     * @param clazz
     * @param sql
     * @return List<T>
     * @throws QueryException
     */
    public <T extends Object> List<T> selectObjectListBySql(Class<T> clazz, String sql) throws QueryException {
        return this.selectObjectListBySql(clazz, sql, null);
    }

    /**
	 * <p>Method: select object list by sql,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param sql
	 * @param parameters
	 * @return List<T>
	 * @throws QueryException
	 */
	public <T extends Object> List<T> selectObjectListBySql(Class<T> clazz, String sql, String[] parameters) throws QueryException{
		List<T> list=null;
        Cursor cursor=null;
        try{
            MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
            cursor=this.executeQueryBySql(sql, parameters);
            list=SqliteUtil.cursorToObjectList(cursor,clazz,mappingBean,this.sqlProcessor);
        }catch(Exception e){
            throw new QueryException(e);
        }finally{
            if(cursor!=null&&!cursor.isClosed()){
                cursor.close();
            }
        }
        return list;
	}

    /**
     * <p>Method: select object pagination list,has implement</p>
     * @param <T>
     * @param clazz
     * @param page
     * @return List<T>
     * @throws QueryException
     */
    public <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz, Page page) throws QueryException {
        return this.selectObjectPaginationList(clazz, page, null);
    }

    /**
     * <p>Method: select object pagination list,has implement</p>
     * @param <T>
     * @param clazz
     * @param page
     * @param condition
     * @return List<T>
     * @throws QueryException
     */
    public <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz, Page page, String condition) throws QueryException {
        return this.selectObjectPaginationList(clazz, page, null, null, condition);
    }

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
	public <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz,Page page,String condition,String[] parameters) throws QueryException{
		return this.selectObjectPaginationList(clazz, page, null, null, condition, parameters);
	}

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
    public <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz, Page page, String[] selectColumns, String table, String condition) throws QueryException {
    	return this.selectObjectPaginationList(clazz, page, selectColumns, table, condition, null);
    }

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
	public <T extends Object> List<T> selectObjectPaginationList(Class<T> clazz, Page page, String[] selectColumns, String table, String condition, String[] parameters) throws QueryException{
		int totalRows=this.totalRows(clazz,table,condition,parameters);
		int rowsPerPage=page.getRowsPerPage();
		page.initialize(totalRows, rowsPerPage);
		int startRow=page.getPageFirstRow();
		StringBuilder sqlConditions=new StringBuilder();
		if(condition!=null){
			sqlConditions.append(condition);
		}
		sqlConditions.append(" "+Constants.Database.MySql.PAGINATION+" ");
		sqlConditions.append(startRow+Constants.Symbol.COMMA+rowsPerPage);
		List<T> list=null;
		try{
			list=this.selectObjectList(clazz, selectColumns, table, sqlConditions.toString(), parameters);
		}catch(Exception e){
			throw new QueryException(e);
		}
		return list;
	}

    /**
	 * <p>Method: execute all sql not include select</p>
	 * @param sql
	 * @throws QueryException 
	 */
	public void executeBySql(String sql) throws QueryException {
	    this.executeBySql(sql,null);
	}

	/**
	 * <p>Method: execute all sql not include select</p>
	 * @param sql
	 * @param parameters
	 * @throws QueryException
	 */
	public void executeBySql(String sql, String[] parameters) throws QueryException {
	    sql=DatabaseMappingUtil.parseSql(sql);
	    logger.info(sql);
	    if(parameters==null||parameters.length==0){
	        this.sqliteDatabase.execSQL(sql);
	    }else{
	        this.sqliteDatabase.execSQL(sql,parameters);
	    }
	}

	/**
     * <p>Method: execute query base on the sql command</p>
     * @param sql
     * @return Cursor
     * @throws QueryException
     */
    public Cursor executeQueryBySql(String sql) throws QueryException {
        return this.executeQueryBySql(sql,null);
    }

    /**
     * <p>Method: execute query base on the sql command</p>
     * @param sql
     * @param parameters
     * @return Cursor
     * @throws QueryException
     */
    public Cursor executeQueryBySql(String sql, String[] parameters) throws QueryException {
        sql=DatabaseMappingUtil.parseSql(sql);
        logger.info(sql);
        return this.sqliteDatabase.rawQuery(sql, parameters);
    }

    /**
	 * <p>Method: execute update</p>
	 * @param object
	 * @param table
	 * @param executeType
	 * @throws QueryException
	 */
	public <T extends Object> void executeUpdate(T object,String table,String condition,ExecuteType executeType) throws QueryException{
	    try {
	        String sql=null;
	        Class<?> clazz=object.getClass();
	        MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
	        List<Object> parameters=new ArrayList<Object>();
	        switch(executeType){
	        case INSERT:
	            sql = SqlInjectUtil.objectToInsertSql(object,table,mappingBean,parameters);
	            break;
	        case UPDATE_BY_ID:
	            sql = SqlInjectUtil.objectToUpdateSql(object,table,condition,true,mappingBean,parameters);
	            break;
	        case UPDATE_NOT_BY_ID:
	            sql = SqlInjectUtil.objectToUpdateSql(object,table,condition,false,mappingBean,parameters);
	            break;
	        case DELETE_BY_ID:
	            sql = SqlInjectUtil.objectToDeleteSql(object,table,condition,true,mappingBean,parameters);
	            break;
	        case DELETE_NOT_BY_ID:
	            sql = SqlInjectUtil.objectToDeleteSql(object,table,condition,false,mappingBean,parameters);
	            break;
	        }
	        String[] parameterArray=null;
	        if(this.sqlProcessor!=null){
	            parameterArray=this.sqlProcessor.parameterListToStringArray(parameters);
	        }
	        this.executeBySql(sql, parameterArray);
	    } catch (Exception e) {
	        throw new QueryException(e);
	    }
	}

	/**
	 * <p>Method: execute update collection by pool name,transaction</p>
	 * @param <T>
	 * @param collection
	 * @param table
	 * @param executeType
	 * @throws QueryException
	 */
	protected <T extends Object> void executeUpdate(Collection<T> collection, String table, ExecuteType executeType) throws QueryException{
	    if(collection!=null){
	        try {
	            int i=0;
	            String[] sqls=new String[collection.size()];
	            for(T object:collection){
	                Class<?> clazz=object.getClass();
	                MappingBean mappingBean=AndroidConfigurationFactory.findMappingBean(clazz);
	                switch(executeType){
	                case INSERT:
	                    sqls[i] = SqlUtil.objectToInsertSql(object,null,mappingBean,this.sqlProcessor);
	                    break;
	                case UPDATE_BY_ID:
	                    sqls[i] = SqlUtil.objectToUpdateSql(object,null,null,true,mappingBean,this.sqlProcessor);
	                    break;
	                case UPDATE_NOT_BY_ID:
	                    break;
	                case DELETE_BY_ID:
	                    sqls[i] = SqlUtil.objectToDeleteSql(object, null, null, true, mappingBean, this.sqlProcessor);
	                    break;
	                case DELETE_NOT_BY_ID:
	                	sqls[i] = SqlUtil.objectToDeleteSql(object, null, null, false, mappingBean, this.sqlProcessor);
	                    break;
	                }
	                i++;
	            }
	            this.executeBatch(sqls);
	        } catch (Exception e) {
	            throw new QueryException(e);
	        }
	    }
	}

	/**
	 * <p>Method: execute update collection,transaction,for sql binding</p>
	 * @param <T>
	 * @param <M>
	 * @param collection
	 * @param clazz
	 * @param table
	 * @param executeType
	 * @return int[]
	 * @throws QueryException
	 */
	protected <T extends Object,M extends Object> void executeUpdate(Collection<T> collection,Class<M> clazz,String table,ExecuteType executeType) throws QueryException{
		if(collection!=null&&!collection.isEmpty()){
			try {
				MappingBean mappingBean=ConfigurationFactory.findMappingBean(clazz);
				List<String> fieldNameList=new ArrayList<String>();
				String sql=null;
				switch(executeType){
				case INSERT:
					sql=SqlInjectUtil.classToInsertSql(clazz, table, mappingBean, fieldNameList);
					break;
				case UPDATE_BY_ID:
					sql=SqlInjectUtil.classToUpdateSql(clazz, table, null, true, mappingBean, fieldNameList);
					break;
				case UPDATE_NOT_BY_ID:
					sql=SqlInjectUtil.classToUpdateSql(clazz, table, null, false, mappingBean, fieldNameList);
					break;
				case DELETE_BY_ID:
					sql=SqlInjectUtil.classToDeleteSql(clazz, table, null, true, mappingBean, fieldNameList);
					break;
				case DELETE_NOT_BY_ID:
					sql=SqlInjectUtil.classToDeleteSql(clazz, table, null, false, mappingBean, fieldNameList);
					break;
				}
				sql=DatabaseMappingUtil.parseSql(sql);
				logger.info(sql);
				this.sqliteDatabase.beginTransaction();
				for(T object:collection){
					if(fieldNameList!=null){
						List<Object> parameterList=new ArrayList<Object>();
						for(String fieldName:fieldNameList){
							Object parameter=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
							parameterList.add(parameter);
						}
						String[] parameters=this.sqlProcessor.parameterListToStringArray(parameterList);
						this.executeBySql(sql, parameters);
					}
				}
				this.sqliteDatabase.setTransactionSuccessful();
			}catch(Exception e){
				throw new QueryException(e);
			}finally{
				this.sqliteDatabase.endTransaction();
			}
		}
	}

	/**
     * <p>Method: execute batch</p>
     * @param sqls
     * @throws QueryException
     */
    public void executeBatch(String[] sqls) throws QueryException {
        try{
            this.sqliteDatabase.beginTransaction();
            for(String sql:sqls){
                this.executeBySql(sql);
            }
            this.sqliteDatabase.setTransactionSuccessful();
        }finally{
            this.sqliteDatabase.endTransaction();
        }
    }

    /**
	 * <p>Method: table total rows</p>
	 * @param table
	 * @return int
	 * @throws QueryException
	 */
	public int totalRows(String table) throws QueryException{
		return this.totalRows(table, null);
	}

	/**
	 * <p>count table total rows</p>
	 * @param table
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public int totalRows(String table,String condition) throws QueryException{
		return this.totalRows(table, condition, null);
	}

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param table
	 * @param condition
	 * @param parameters
	 * @return int
	 * @throws QueryException
	 */
	public <T extends Object> int totalRows(String table,String condition,String[] parameters) throws QueryException{
		return this.totalRows(null, table, condition,parameters);
	}

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @return int
	 * @throws QueryException
	 */
	public <T extends Object> int totalRows(Class<T> clazz) throws QueryException{
		return this.totalRows(clazz, null);
	}

	/**
	 * <p>Method; get the total size,it is sql binding</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public <T extends Object> int totalRows(Class<T> clazz,String condition) throws QueryException{
		return this.totalRows(clazz, null ,condition);
	}

	/**
	 * <p>Method; get the total size</p>
	 * @param <T>
	 * @param clazz
	 * @param condition
	 * @param parameters
	 * @return int
	 * @throws QueryException
	 */
	public <T extends Object> int totalRows(Class<T> clazz,String condition,String[] parameters) throws QueryException{
		return 0;
	}

	/**
	 * <p>Method; get the total size</p>
	 * @param <T>
	 * @param clazz
	 * @param table
	 * @param condition
	 * @return int
	 * @throws QueryException
	 */
	public <T extends Object> int totalRows(Class<T> clazz,String table,String condition) throws QueryException{
		return this.totalRows(clazz, table, condition, null);
	}

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
	public <T extends Object> int totalRows(Class<T> clazz,String table,String condition,String[] parameters) throws QueryException{
		int totalRows=0;
		Cursor cursor=null;
        try {
        	String sql=null;
        	if(clazz!=null){
				MappingBean mappingBean=ConfigurationFactory.findMappingBean(clazz);
				sql=SqlUtil.selectSql(new String[]{"COUNT(0) AS "+Constants.Database.COLUMN_NAME_TOTAL},table,condition,mappingBean);
			}else{
				sql=SqlUtil.selectSql(new String[]{"COUNT(0) AS "+Constants.Database.COLUMN_NAME_TOTAL},table,condition,null);
			}
        	cursor=this.executeQueryBySql(sql,parameters);
        	int rowCount = cursor.getCount();
        	if(rowCount>0){
        		cursor.moveToFirst();
        		totalRows=cursor.getInt(cursor.getColumnIndex(Constants.Database.COLUMN_NAME_TOTAL));
        	}
        }catch(Exception e) {
            throw new QueryException(e);
        }finally{
            if(cursor!=null&&!cursor.isClosed()){
                cursor.close();
            }
        }
		return totalRows;
	}

	/**
     * @param sqlProcessor the sqlProcessor to set
     */
    public void setSqlProcessor(SqlProcessor sqlProcessor) {
        this.sqlProcessor = sqlProcessor;
    }
}
