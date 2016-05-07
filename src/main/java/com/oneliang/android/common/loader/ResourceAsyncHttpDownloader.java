package com.oneliang.android.common.loader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.oneliang.frame.broadcast.BroadcastReceiver;
import com.oneliang.util.http.HttpDownloader.DownloadListener;
import com.oneliang.util.http.HttpUtil.HttpNameValue;

/**
 * did not support cache loader,only can read resource from remote.
 * @param <T>
 */
public abstract class ResourceAsyncHttpDownloader<T extends Object> extends AbstractResourceAsyncHttpDownloader<T> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -581799242885252127L;

    protected final Map<String,Boolean> resourceLoadingMap=new ConcurrentHashMap<String, Boolean>();

    public ResourceAsyncHttpDownloader(int minThreads,int maxThreads) {
        super(minThreads,maxThreads);
    }

    /**
     * download
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param fullFilename
     * @param fileDownloadCallback
     */
    public void download(final String httpUrl,List<HttpNameValue> httpHeaderList,final int timeout,final String fullFilename,final ResourceLoadCallback<T> resourceLoadCallback){
        this.download(httpUrl, httpHeaderList, timeout, fullFilename, null, resourceLoadCallback);
    }


    /**
     * download while judge the is it downloading,if downloading then wait.
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param fullFilename
     * @param fileDownloadListener
     * @param fileDownloadCallback
     */
    public void download(final String httpUrl,List<HttpNameValue> httpHeaderList,final int timeout,final String fullFilename,DownloadListener resourceDownloadListener,final ResourceLoadCallback<T> resourceLoadCallback){
        Boolean loading=this.resourceLoadingMap.get(fullFilename);
        if(loading!=null&&loading.booleanValue()){
            //waiting for loading and register the receiver wait for broadcast
            System.out.println("waiting...");
            BroadcastReceiver broadcastReceiver=new ResourceBroadcastReceiver(fullFilename,resourceLoadCallback);
            resourceLoadBroadcastManager.registerBroadcastReceiver(new String[]{ACTION_LOAD_RESOURCE_FINISH,ACTION_LOAD_RESOURCE_FAILURE}, broadcastReceiver); 
        }else{
            if(loading==null){
                this.resourceLoadingMap.put(fullFilename, true);
            }
            this.httpDownload(httpUrl, httpHeaderList, timeout, fullFilename, resourceDownloadListener, resourceLoadCallback);
        }
    }

    /**
     * load resource finish
     * @param fullFilename
     * @param resource
     * @param resourceLoadType
     */
    protected void loadResourceFinish(final String fullFilename,final T resource,int resourceLoadType){
        this.resourceLoadingMap.put(fullFilename, false);
        super.loadResourceFinish(fullFilename, resource,resourceLoadType);
    }

    /**
     * load resource failure
     * @param fullFilename
     * @param resourceLoadType
     */
    protected void loadResourceFailure(final String fullFilename,int resourceLoadType){
        this.resourceLoadingMap.remove(fullFilename);
        super.loadResourceFailure(fullFilename, resourceLoadType);
    }
}
