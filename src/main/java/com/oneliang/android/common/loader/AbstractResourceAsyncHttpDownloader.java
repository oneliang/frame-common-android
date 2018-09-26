package com.oneliang.android.common.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.oneliang.Constants;
import com.oneliang.android.common.broadcast.BroadcastManager;
import com.oneliang.frame.broadcast.BroadcastReceiver;
import com.oneliang.frame.broadcast.Message;
import com.oneliang.util.http.AsyncHttpDownloader;
import com.oneliang.util.http.HttpDownloader.DownloadListener;
import com.oneliang.util.http.HttpUtil.HttpNameValue;

/**
 * did not support cache loader,only can read resource from remote.
 * @param <T>
 */
public abstract class AbstractResourceAsyncHttpDownloader<T extends Object> extends AsyncHttpDownloader {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1963225206336685083L;

    protected static final String ACTION_LOAD_RESOURCE_PROGRESS="action.load.resource.progress";
    protected static final String ACTION_LOAD_RESOURCE_FINISH="action.load.resource.finish";
    protected static final String ACTION_LOAD_RESOURCE_FAILURE="action.load.recouece.failure";
    protected static final String KEY_PROGRESS="progress";
    protected static final String KEY_CONTENT_LENGTH="content.length";
    protected static final String KEY_FULL_FILENAME="full.filename";
    protected static final String KEY_RESOURCE="resource";
    protected static final String KEY_RESOURCE_LOAD_TYPE="resource.load.type";
    protected static final int RESOURCE_LOAD_TYPE_HTTP=0x1;
    protected final int DEFAULT_TIMEOUT=20000;
    protected final BroadcastManager resourceLoadBroadcastManager=new BroadcastManager();

    public AbstractResourceAsyncHttpDownloader(int minThreads,int maxThreads) {
        super(minThreads,maxThreads);
    }

    public void start() {
        super.start();
        resourceLoadBroadcastManager.start();
    }

    /**
     * http download
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param fullFilename
     * @param fileDownloadListener
     * @param fileDownloadCallback
     */
    protected void httpDownload(final String httpUrl,final List<HttpNameValue> httpHeaderList,final int timeout,final String fullFilename,final DownloadListener resourceDownloadListener,final ResourceLoadCallback<T> resourceLoadCallback){
        DownloadListener tempResourceDownloadListener=resourceDownloadListener;
        if(tempResourceDownloadListener==null){
            tempResourceDownloadListener=new DownloadListener(){
                public void onStart() {
                    BroadcastReceiver broadcastReceiver=new ResourceBroadcastReceiver(fullFilename,resourceLoadCallback);
                    resourceLoadBroadcastManager.registerBroadcastReceiver(new String[]{ACTION_LOAD_RESOURCE_PROGRESS,ACTION_LOAD_RESOURCE_FINISH,ACTION_LOAD_RESOURCE_FAILURE}, broadcastReceiver);
                }
                public void onProcess(Map<String,List<String>> headerFieldMap,InputStream inputStream, int contentLength, String saveFile) {
                    try{
                        loadResourceProcess(headerFieldMap, inputStream, contentLength, saveFile,RESOURCE_LOAD_TYPE_HTTP);
                    }catch (Exception e) {
                        onFailure(e);
                    }
                }
                public void onFinish() {
                    try {
                        T resource=loadResourceFromFile(fullFilename);
                        if(resource!=null){
                            loadResourceFinish(fullFilename,resource,RESOURCE_LOAD_TYPE_HTTP);
                        }
                    } catch (Exception e) {
                        onFailure(e);
                    }
                }
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                    loadResourceFailure(fullFilename,RESOURCE_LOAD_TYPE_HTTP);
                }
            };
        }
        super.download(httpUrl, httpHeaderList, null, timeout,fullFilename,tempResourceDownloadListener);
    }

    /**
     * load resource from file
     * @param fullFilename
     * @throws Exception
     * @return T
     */
    protected abstract T loadResourceFromFile(final String fullFilename) throws Exception;

    /**
     * load resource process
     * @param headerFieldMap
     * @param inputStream
     * @param contentLength
     * @param saveFile
     * @param resourceLoadType
     * @throws Exception
     */
    protected void loadResourceProcess(Map<String,List<String>> headerFieldMap,InputStream inputStream, int contentLength, String saveFile,int resourceLoadType) throws Exception{
        File file=new File(saveFile);
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        byte[] buffer = new byte[Constants.Capacity.BYTES_PER_KB];
        long current=0;
        int length=-1;
        int lastProgress=0;
        while((length=inputStream.read(buffer,0,buffer.length))!=-1){
            fileOutputStream.write(buffer,0,length);
            fileOutputStream.flush();
            current+=length;
            int progress=(int)(current*100/contentLength);
            if(progress>lastProgress){
                lastProgress=progress;
                Message message=new Message();
                message.addAction(ACTION_LOAD_RESOURCE_PROGRESS);
                message.putObject(KEY_FULL_FILENAME, saveFile);
                message.putObject(KEY_PROGRESS, progress);
                message.putObject(KEY_CONTENT_LENGTH, contentLength);
                message.putObject(KEY_RESOURCE_LOAD_TYPE,resourceLoadType);
                this.resourceLoadBroadcastManager.sendBroadcast(message);
            }
        }
        fileOutputStream.close();
    }
    /**
     * load resource finish
     * @param fullFilename
     * @param resource
     * @param resourceLoadType
     */
    protected void loadResourceFinish(final String fullFilename,final T resource,int resourceLoadType){
        final Message message=new Message();
        message.addAction(ACTION_LOAD_RESOURCE_FINISH);
        message.putObject(KEY_FULL_FILENAME, fullFilename);
        message.putObject(KEY_RESOURCE, resource);
        message.putObject(KEY_RESOURCE_LOAD_TYPE,resourceLoadType);
        this.resourceLoadBroadcastManager.sendBroadcast(message);
    }

    /**
     * load resource failure
     * @param fullFilename
     * @param resourceLoadType
     */
    protected void loadResourceFailure(final String fullFilename,int resourceLoadType){
        final Message message=new Message();
        message.addAction(ACTION_LOAD_RESOURCE_FAILURE);
        message.putObject(KEY_FULL_FILENAME, fullFilename);
        message.putObject(KEY_RESOURCE_LOAD_TYPE,resourceLoadType);
        this.resourceLoadBroadcastManager.sendBroadcast(message);
    }

    protected final class ResourceBroadcastReceiver implements BroadcastReceiver{
        private String fullFilename=null;
        private ResourceLoadCallback<T> resourceLoadCallback=null;
        public ResourceBroadcastReceiver(String fullFilename,ResourceLoadCallback<T> resourceLoadCallback) {
            this.fullFilename=fullFilename;
            this.resourceLoadCallback=resourceLoadCallback;
        }
        public void receive(String action,Message message) {
            if(action!=null){
                String filename=(String)message.getObject(KEY_FULL_FILENAME);
                Integer resourceLoadTypeObject=(Integer)message.getObject(KEY_RESOURCE_LOAD_TYPE);
                int resourceLoadType=((Integer)resourceLoadTypeObject).intValue();
                if(action.equals(ACTION_LOAD_RESOURCE_PROGRESS)){
                    Object progressObject=message.getObject(KEY_PROGRESS);
                    Object contentLengthObject=message.getObject(KEY_CONTENT_LENGTH);
                    if(filename.equals(fullFilename)){
                        if(progressObject!=null&&progressObject instanceof Integer&&contentLengthObject!=null&&contentLengthObject instanceof Integer){
                            int progress=((Integer)progressObject).intValue();
                            int contentLength=((Integer)contentLengthObject).intValue();
                            this.resourceLoadCallback.processCallback(progress,contentLength,resourceLoadType);
                        }
                    }
                }if(action.equals(ACTION_LOAD_RESOURCE_FINISH)){
                    if(filename.equals(fullFilename)){
                        @SuppressWarnings("unchecked")
                        T resource=(T)message.getObject(KEY_RESOURCE);
                        this.resourceLoadCallback.successCallback(resource,resourceLoadType);
                        resourceLoadBroadcastManager.unregisterBroadcastReceiver(this);
                    }
                }else if(action.equals(ACTION_LOAD_RESOURCE_FAILURE)){
                    if(filename.equals(fullFilename)){
                        this.resourceLoadCallback.failureCallback(resourceLoadType);
                        resourceLoadBroadcastManager.unregisterBroadcastReceiver(this);
                    }
                }
            }
        }
    }

    public abstract static interface ResourceLoadCallback<T extends Object>{
        public abstract void processCallback(int progress,int contentLength,int resourceLoadType);
        public abstract void successCallback(T resource,int resourceLoadType);
        public abstract void failureCallback(int resourceLoadType);
    }
}
