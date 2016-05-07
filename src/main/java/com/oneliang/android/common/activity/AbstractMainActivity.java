package com.oneliang.android.common.activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.oneliang.util.common.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.ViewGroup;

public abstract class AbstractMainActivity extends Activity {

	protected ActivityViewManager activityViewManager=new ActivityViewManager();
    protected final List<AbstractActivityView> activityViewList=new CopyOnWriteArrayList<AbstractActivityView>();
    protected AbstractActivityView currentActivityView=null;
    protected Handler handler=new Handler(){
    	public void handleMessage(android.os.Message message) {
    		if(message.obj!=null&&message.obj instanceof AbstractActivityView){
    			AbstractActivityView activityView=(AbstractActivityView)message.obj;
    			if(activityView!=null){
                	activityViewList.add(activityView);
                	activityView.onCreate();
                    setContentView(activityView.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    currentActivityView=activityView;
                }
    		}
    	};
    };

    void addCreateActivityView(AbstractActivityView abstractActivityView){
    	android.os.Message message=android.os.Message.obtain();
    	message.obj=abstractActivityView;
    	handler.sendMessage(message);
    }

	/**
     * start activity view
     * @param activityViewId
     */
    protected void startActivityView(String activityViewId){
    	startActivityView(activityViewId, Integer.MIN_VALUE, null);
    }

    /**
     * start activity view
     * @param activityViewId
     * @param requestCode
     * @param data
     */
    protected void startActivityView(String activityViewId,int requestCode,Object data){
    	String className=activityViewManager.getActivityViewClass(activityViewId);
    	if(StringUtil.isNotBlank(className)){
    		try {
    			Class<?> clazz=Thread.currentThread().getContextClassLoader().loadClass(className);
        		Constructor<?> constructor=clazz.getConstructor(Context.class);
        		Object object=constructor.newInstance(this);
        		if(object!=null&&object instanceof AbstractActivityView){
        			AbstractActivityView activityView =(AbstractActivityView)object;
        			activityView.setRequestCode(requestCode);
        			activityView.setData(data);
        			addCreateActivityView(activityView);
        		}
    		} catch (SecurityException e) {
    			e.printStackTrace();
    		} catch (NoSuchMethodException e) {
    			e.printStackTrace();
    		} catch (IllegalArgumentException e) {
    			e.printStackTrace();
    		} catch (InstantiationException e) {
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		} catch (InvocationTargetException e) {
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * finish the activity view
     * @return boolean
     */
    protected boolean finishActivityView() {
    	return finishActivityView(null);
    }

    /**
     * finish the activity view with data
     * @param data
     * @return boolean
     */
    protected boolean finishActivityView(Object data) {
    	boolean canFinished=false;
        if(this.activityViewList.size()>1){
        	AbstractActivityView activityView=this.activityViewList.remove(this.activityViewList.size()-1);
        	int requestCode=Integer.MIN_VALUE;
        	int responseCode=AbstractActivityView.RESPONSE_CODE_CANCELED;
        	if(activityView!=null){
        		requestCode=activityView.getRequestCode();
        		responseCode=activityView.getResponseCode();
        		activityView.onDestroy();
        	}
            activityView=this.activityViewList.get(this.activityViewList.size()-1);
            if (activityView!=null) {
                this.setContentView(activityView.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                activityView.onActivityViewResponse(requestCode,responseCode,data);
                this.currentActivityView=activityView;
            }
            canFinished=true;
        }
        return canFinished;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
    	return super.dispatchKeyEvent(event);
    }
}
