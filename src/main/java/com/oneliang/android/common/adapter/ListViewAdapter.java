package com.oneliang.android.common.adapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class ListViewAdapter<M extends Object> extends BaseAdapter implements Filterable{

    private List<M> modelList=new CopyOnWriteArrayList<M>();
    private ViewProcessor<M> viewProcessor=null;
    private Filter filter=null;

    private Context context=null;

    public ListViewAdapter(Context context,ViewProcessor<M> viewProcessor) {
        this.context=context;
        this.viewProcessor=viewProcessor;
    }

    public ListViewAdapter(Context context,ViewProcessor<M> viewProcessor,Filter filter) {
        this.context=context;
        this.viewProcessor=viewProcessor;
        this.filter=filter;
    }

    public int getCount() {
        return this.modelList.size();
    }

    public M getItem(int position) {
        return this.modelList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        M model=this.modelList.get(position);
        if(convertView==null){
            convertView=View.inflate(this.context, this.viewProcessor.getResourceId(position,model), null);
            convertView.setTag(convertView);
        }else{
            convertView=(View)convertView.getTag();
        }
        this.viewProcessor.afterGetViewProcess(convertView, position, model);
        return convertView;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public int getItemViewType(int position) {
        M model=this.getItem(position);
        return this.viewProcessor.getViewType(position,model);
    }

    public int getViewTypeCount() {
        return this.viewProcessor.getViewTypeCount();
    }

    /**
     * add model
     * @param model
     */
    public void addModel(M model){
        this.modelList.add(model);
    }

    /**
     * add model list
     * @param modelList
     */
    public void addModelList(List<M> modelList){
        this.modelList.addAll(modelList);
    }

    /**
     * set model list
     * @param modelList
     */
    public void setModelList(List<M> modelList){
        this.modelList.clear();
        this.modelList.addAll(modelList);
    }

    /**
     * get model
     * @param position
     * @return M
     */
    public M getModel(int position){
        return this.modelList.get(position);
    }

    /**
     * ViewProcessor
     * @param <M>
     */
    public static abstract class ViewProcessor<M extends Object>{
        public abstract void afterGetViewProcess(View view,int position,M model);
        public abstract int getResourceId(int position,M model);
        public int getViewType(int position,M model) {
            return 0;
        }
        public int getViewTypeCount() {
            return 1;
        }
    }
}
