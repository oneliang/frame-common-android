package com.oneliang.android.common.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.oneliang.Constant;
import com.oneliang.frame.broadcast.BroadcastReceiver;
import com.oneliang.util.common.Encoder;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.concurrent.ThreadPool;
import com.oneliang.util.concurrent.ThreadTask;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.http.HttpDownloader.DownloadListener;
import com.oneliang.util.http.HttpUtil.HttpNameValue;

/**
 * support cache loader,can read resource from http remote,memory cache, file cache and exist file path.
 * for simple file,image file or little file.
 * @param <T>
 */
public abstract class ResourceAsyncLoader<T extends Object> extends AbstractResourceAsyncHttpDownloader<T>{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1650132644462548755L;

    public static final int RESOURCE_LOAD_TYPE_HTTP=0x1;
    public static final int RESOURCE_LOAD_TYPE_LOCAL=0x2;
    public static final int RESOURCE_LOAD_TYPE_MEMORY=0x3;
    private static final int DEFAULT_LOCAL_MIN_THREADS=1;
    private static final int DEFAULT_LOCAL_MAX_THREADS=1;
    private static final String tempFileName="temp";
    private final ThreadPool localThreadPool=new ThreadPool();
    protected final Map<String,Boolean> resourceLoadingMap=new ConcurrentHashMap<String, Boolean>();
    protected final Map<String,SoftReference<T>> resourceMap=new ConcurrentHashMap<String, SoftReference<T>>();
    private final Map<String,Long> resourceFinishedMap=new ConcurrentHashMap<String, Long>();
    private String httpDownloadCachePath=null;

    public ResourceAsyncLoader(int localMinThreads,int localMaxThreads,int minHttpDownloadThreads,int maxHttpDownloadThreads) {
        super(minHttpDownloadThreads,maxHttpDownloadThreads);
        localThreadPool.setMinThreads(localMinThreads<0?DEFAULT_LOCAL_MIN_THREADS:localMinThreads);
        localThreadPool.setMaxThreads(localMaxThreads<0?DEFAULT_LOCAL_MAX_THREADS:localMaxThreads);
        this.httpDownloadCachePath=this.getHttpDownloadCachePath();
        if(this.httpDownloadCachePath==null){
            throw new NullPointerException("getHttpDownloadCachePath() cat not return null.");
        }else{
            try {
                readTempFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readTempFile() throws Exception{
        File tempFile=new File(this.httpDownloadCachePath,tempFileName);
        if(tempFile.exists()){
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
            String line=null;
            while((line=bufferedReader.readLine())!=null){
                if(StringUtil.isNotBlank(line)){
                    int index=line.lastIndexOf(Constant.Symbol.EQUAL);
                    if(index>=0){
                        String filename=line.substring(0, index);
                        long fileLength=Long.parseLong(line.substring(index+Constant.Symbol.EQUAL.length(), line.length()));
                        resourceFinishedMap.put(filename, fileLength);
                    }
                }
            }
        }else{
            FileUtil.createFile(tempFile.getAbsolutePath());
        }
    }

    public void start() {
        super.start();
        this.localThreadPool.start();
    }

    /**
     * load resource ,from local
     * @param filePath
     * @param resourceLoadCallback not null
     */
    public void loadResourceFromLocal(final String filePath,final ResourceLoadCallback<T> resourceLoadCallback){
        loadResource(filePath, null, 0, null, resourceLoadCallback, RESOURCE_LOAD_TYPE_LOCAL);
    }

    /**
     * load resource timeout default 20s,with http
     * @param httpUrl
     * @param httpHeaderList
     * @param resourceLoadCallback not null
     */
    public void loadResource(final String httpUrl,List<HttpNameValue> httpHeaderList,final ResourceLoadCallback<T> resourceLoadCallback){
        loadResource(httpUrl, httpHeaderList, DEFAULT_TIMEOUT, resourceLoadCallback);
    }

    /**
     * load resource with http
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param resourceLoadCallback not null
     */
    public void loadResource(final String httpUrl,List<HttpNameValue> httpHeaderList,final int timeout,final ResourceLoadCallback<T> resourceLoadCallback){
        loadResource(httpUrl, httpHeaderList, timeout, null, resourceLoadCallback);
    }

    /**
     * load resource with http
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param resourceDownloadListener
     * @param resourceLoadCallback not null
     */
    public void loadResource(final String httpUrl,List<HttpNameValue> httpHeaderList,final int timeout,DownloadListener resourceDownloadListener,final ResourceLoadCallback<T> resourceLoadCallback){
        loadResource(httpUrl, httpHeaderList, timeout, resourceDownloadListener, resourceLoadCallback, RESOURCE_LOAD_TYPE_HTTP);
    }

    /**
     * load resource
     * @param url,if for local url is filePath,if for http url is http url
     * @param httpHeaderList
     * @param timeout
     * @param resourceDownloadListener
     * @param resourceLoadCallback not null
     * @param loadResourceType for local or http
     */
    protected void loadResource(final String url,List<HttpNameValue> httpHeaderList,final int timeout,final DownloadListener resourceDownloadListener,final ResourceLoadCallback<T> resourceLoadCallback,final int loadResourceType){
        String tempFullFilename=null;
        int tempLoadResourceType=loadResourceType;
        if(loadResourceType==RESOURCE_LOAD_TYPE_LOCAL){
            tempFullFilename=url;
        }else if(loadResourceType==RESOURCE_LOAD_TYPE_HTTP){//load resource
            final String filename=Encoder.escape(url,new char[]{Constant.Symbol.DOT_CHAR});
            tempFullFilename=this.httpDownloadCachePath+filename;
            File filePath=new File(this.httpDownloadCachePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        }
        final String fullFilename=tempFullFilename;
        boolean needToLoadResource=false;
        Boolean loading=this.resourceLoadingMap.get(fullFilename);
        if(loading!=null&&loading.booleanValue()){
            //waiting for loading and register the receiver wait for broadcast
            System.out.println("waiting...");
            BroadcastReceiver broadcastReceiver=new ResourceBroadcastReceiver(fullFilename,resourceLoadCallback);
            this.resourceLoadBroadcastManager.registerBroadcastReceiver(new String[]{ACTION_LOAD_RESOURCE_FINISH,ACTION_LOAD_RESOURCE_FAILURE}, broadcastReceiver); 
        }else{
            //had load yet
            T resource=null;
            if(this.resourceMap.containsKey(fullFilename)){//memory has load record
                resource=this.resourceMap.get(fullFilename).get();
                if(resource!=null){//from memory cache
                    tempLoadResourceType=RESOURCE_LOAD_TYPE_MEMORY;
                    resourceLoadCallback.successCallback(resource,tempLoadResourceType);
                    System.out.println("from memory cache...");
                }else{//from file cache
                    //need to load file from local
                    needToLoadResource=true;
                    tempLoadResourceType=RESOURCE_LOAD_TYPE_LOCAL;
                    System.out.println("1 cache from file:"+fullFilename);
                }
            }else{//memory has no record
                File file=new File(fullFilename);
                if(file.exists()){//memory has no record,but has file.
                    //need to load file from local
                    needToLoadResource=true;
                    if(loadResourceType==RESOURCE_LOAD_TYPE_LOCAL){
                        tempLoadResourceType=RESOURCE_LOAD_TYPE_LOCAL;
                        System.out.println("2 cache from file:"+fullFilename);
                    }else if(loadResourceType==RESOURCE_LOAD_TYPE_HTTP){
                        if(this.resourceFinishedMap.containsKey(fullFilename)){
                            tempLoadResourceType=RESOURCE_LOAD_TYPE_LOCAL;
                            System.out.println("2 cache from file:"+fullFilename);
                        }else{
                            tempLoadResourceType=RESOURCE_LOAD_TYPE_HTTP;//maybe download suspend
                            System.out.println("http reload:"+url);
                        }
                    }
                }else{//memory has no record,and has no file,need to load resource from http
                    needToLoadResource=true;
                    tempLoadResourceType=RESOURCE_LOAD_TYPE_HTTP;
                    System.out.println("http load:"+url);
                }
            }
        }
        if(needToLoadResource){
            if(loading==null){
                this.resourceLoadingMap.put(fullFilename, true);
            }
            if(tempLoadResourceType==RESOURCE_LOAD_TYPE_LOCAL){
                this.localThreadPool.addThreadTask(new ThreadTask(){
                    private static final long serialVersionUID = 8302070257111882824L;
                    public void runTask() {
                        try{
                            BroadcastReceiver broadcastReceiver=new ResourceBroadcastReceiver(fullFilename,resourceLoadCallback);
                            resourceLoadBroadcastManager.registerBroadcastReceiver(new String[]{ACTION_LOAD_RESOURCE_FINISH,ACTION_LOAD_RESOURCE_FAILURE}, broadcastReceiver);
                            T resource=loadResourceFromFile(fullFilename);
                            if(resource!=null){
                                loadResourceFinish(fullFilename,resource,RESOURCE_LOAD_TYPE_LOCAL);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            loadResourceFailure(fullFilename,RESOURCE_LOAD_TYPE_LOCAL);
                        }
                    }
                });
            }else if(tempLoadResourceType==RESOURCE_LOAD_TYPE_HTTP){
                super.httpDownload(url, httpHeaderList, timeout, fullFilename, resourceDownloadListener, resourceLoadCallback);
            }
        }
    }

    /**
     * load resource finish
     * @param fullFilename
     * @param resource
     * @param resourceLoadType
     */
    protected void loadResourceFinish(final String fullFilename,final T resource,int resourceLoadType){
        this.resourceMap.put(fullFilename, new SoftReference<T>(resource));
        this.resourceLoadingMap.put(fullFilename, false);
        if(resourceLoadType==RESOURCE_LOAD_TYPE_HTTP){
            this.addResourceFinished(fullFilename);
        }
        super.loadResourceFinish(fullFilename, resource, resourceLoadType);
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

    /**
     * add resource finished
     * @param fullFilename
     */
    public void addResourceFinished(String fullFilename){
        if(!this.resourceFinishedMap.containsKey(fullFilename)){
            File file=new File(fullFilename);
            long fileLength=file.length();
            this.resourceFinishedMap.put(fullFilename, fileLength);
            String line=fullFilename+Constant.Symbol.EQUAL+fileLength+StringUtil.CRLF_STRING;
            try {
                OutputStream outputStream=new FileOutputStream(new File(this.httpDownloadCachePath,tempFileName),true);
                outputStream.write(line.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get cache path
     * @return String,can not be null.
     */
    protected abstract String getHttpDownloadCachePath();

    /**
     * clean cache 
     * @throws Exception
     */
    protected void cleanHttpDownloadCache(){
        File file=new File(this.httpDownloadCachePath);
        if(file.exists()){
            file.delete();
        }
    }
}
