package com.oneliang.android.common.activity;

import android.content.Context;
import android.view.View;

public abstract class AbstractActivityView{

	public static final int RESPONSE_CODE_SUCCESS=0;
	public static final int RESPONSE_CODE_FAILURE=1;
	public static final int RESPONSE_CODE_CANCELED=2;
    protected Context context=null;
    int requestCode=Integer.MIN_VALUE;
    Object data=null;
    int responseCode=RESPONSE_CODE_CANCELED;

    public AbstractActivityView(Context context) {
    	if(context==null){
    		throw new NullPointerException("context can not be null");
    	}
    	this.context=context;
	}

    /**
     * on create
     */
    protected void onCreate(){
    	
    }

    /**
     * on destroy
     */
    protected void onDestroy(){
    	this.context=null;
    }

    /**
     * on activity view response
     * @param requestCode
     * @param responseCode
     * @param data
     */
    protected void onActivityViewResponse(int requestCode,int responseCode,Object data){
    	
    }

    /**
     * start activity view
     * @param activityViewId
     */
    protected void startActivityView(String activityViewId){
    	this.startActivityView(activityViewId,Integer.MIN_VALUE,null);
    }

    /**
     * start activity view
     * @param activityViewId
     */
    protected void startActivityView(String activityViewId,int requestCode,Object data){
    	if(context!=null&&(context instanceof AbstractMainActivity)){
            AbstractMainActivity abstractMainActivity=(AbstractMainActivity)context;
            abstractMainActivity.startActivityView(activityViewId,requestCode,data);
        }
    }

    /**
     * get view
     * @return View
     */
    protected abstract View getView();

    /**
     * request code
     * @param requestCode
     */
    void setRequestCode(int requestCode){
    	this.requestCode=requestCode;
    }

    /**
     * get request code
     * @return int
     */
    int getRequestCode(){
    	return this.requestCode;
    }

    /**
     * set data
     * @param data
     */
    void setData(Object data){
    	this.data=data;
    }

    /**
     * get data
     * @return Object
     */
    protected Object getData(){
    	return this.data;
    }

    /**
     * set response code
     * @param responseCode
     */
    protected void setResponseCode(int responseCode){
    	this.responseCode=responseCode;
    }

    /**
     * get response code
     * @return int
     */
    int getResponseCode(){
    	return this.responseCode;
    }

    /**
     * finish the activity view
     */
    public void finish(){
    	finish(null);
    }

    /**
     * finish the activity view with data
     * @param data
     */
    public void finish(Object data){
        if(context!=null&&(context instanceof AbstractMainActivity)){
            AbstractMainActivity abstractMainActivity=(AbstractMainActivity)context;
            abstractMainActivity.finishActivityView(data);
        }
    }
}
