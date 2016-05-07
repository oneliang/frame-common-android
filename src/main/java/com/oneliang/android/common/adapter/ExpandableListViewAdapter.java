package com.oneliang.android.common.adapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.oneliang.android.common.adapter.ExpandableListViewAdapter.ExpandableModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class ExpandableListViewAdapter<M extends ExpandableModel<I>,I extends Object> extends BaseExpandableListAdapter {

    private static final String GROUP_CHILD_SEPERATOR="00000000";
    private List<M> modelList=new CopyOnWriteArrayList<M>();
    private ViewProcessor<M,I> viewProcessor=null;

    private Context context=null;

    public ExpandableListViewAdapter(Context context,ViewProcessor<M,I> viewProcessor) {
        this.context=context;
        this.viewProcessor=viewProcessor;
    }

    public int getGroupCount() {
        return this.modelList.size();
    }

    public int getChildrenCount(int groupPosition) {
        int count=0;
        M model=this.modelList.get(groupPosition);
        List<I> itemList=model.getItemList();
        if(itemList!=null){
            count=itemList.size();
        }
        return count;
    }

    public M getGroup(int groupPosition) {
        return this.modelList.get(groupPosition);
    }

    
    public I getChild(int groupPosition, int childPosition) {
        I item=null;
        M model=this.modelList.get(groupPosition);
        List<I> itemList=model.getItemList();
        if(itemList!=null&&0<=childPosition&&childPosition<itemList.size()){
            item=itemList.get(childPosition);
        }
        return item;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return Long.parseLong(groupPosition+GROUP_CHILD_SEPERATOR+childPosition);
    }

    public boolean hasStableIds() {
        return false;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        M model=this.getGroup(groupPosition);
        if(convertView==null){
            convertView=View.inflate(this.context, this.viewProcessor.getGroupResourceId(groupPosition,model), null);
            convertView.setTag(convertView);
        }else{
            convertView=(View)convertView.getTag();
        }
        this.viewProcessor.afterGetGroupViewProcess(convertView, groupPosition, model);
        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        I item=this.getChild(groupPosition, childPosition);
        if(convertView==null){
            convertView=View.inflate(this.context, this.viewProcessor.getChildResourceId(groupPosition,childPosition,item), null);
            convertView.setTag(convertView);
        }else{
            convertView=(View)convertView.getTag();
        }
        this.viewProcessor.afterGetChildViewProcess(convertView, groupPosition, childPosition, item);
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public int getChildType(int groupPosition, int childPosition) {
        I item=this.getChild(groupPosition, childPosition);
        return this.viewProcessor.getChildViewType(groupPosition, childPosition, item);
    }

    public int getChildTypeCount() {
        return this.viewProcessor.getChildViewTypeCount();
    }

    public int getGroupType(int groupPosition) {
        M model=this.modelList.get(groupPosition);
        return this.viewProcessor.getGroupViewType(groupPosition, model);
    }

    public int getGroupTypeCount() {
        return this.viewProcessor.getGroupViewTypeCount();
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

    public static abstract interface ExpandableModel<I extends Object>{
        public List<I> getItemList();
    }
    public static abstract class ViewProcessor<M extends ExpandableModel<I>,I extends Object>{
        public abstract void afterGetGroupViewProcess(View view,int groupPosition,M model);
        public abstract void afterGetChildViewProcess(View view,int groupPosition,int childPosition,I item);
        public abstract int getGroupResourceId(int groupPosition,M model);
        public abstract int getChildResourceId(int groupPosition,int childPosition,I item);
        public int getGroupViewType(int groupPosition,M model) {
            return 0;
        }
        public int getGroupViewTypeCount() {
            return 1;
        }
        public int getChildViewType(int groupPosition,int childPosition,I item) {
            return 0;
        }
        public int getChildViewTypeCount() {
            return 1;
        }
    }
}
