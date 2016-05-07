package com.oneliang.android.common.broadcast;

import android.os.Handler;
import com.oneliang.frame.broadcast.Message;

public final class BroadcastManager extends com.oneliang.frame.broadcast.BroadcastManager{

    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message osMessage) {
            if(osMessage.obj!=null&&osMessage.obj instanceof Message){
                Message message=(Message)osMessage.obj;
                BroadcastManager.this.handleMessage(message);
            }
        };
    };

    public void run() {
        while(!Thread.currentThread().isInterrupted()){
        	try{
                if(!this.messageQueue.isEmpty()){
                    Message message=this.messageQueue.poll();
                    android.os.Message osMessage=android.os.Message.obtain();
                    osMessage.obj=message;
                    handler.sendMessage(osMessage);
                }else{
                    synchronized (this) {
                        this.wait();
                    }
                }
	            
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    }
}
