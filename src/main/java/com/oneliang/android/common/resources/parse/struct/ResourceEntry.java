package com.oneliang.android.common.resources.parse.struct;

import java.util.Arrays;

import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;

public class ResourceEntry implements Comparable<ResourceEntry>{

	public final boolean array;
	public final RType rType;
	public final String name;
	public final int idValue;
	public final String config;
	public final String[] datas;

	public ResourceEntry(boolean array,String type,String name,int idValue,String config,String[] datas) {
		this.rType=RType.valueOf(type.toUpperCase());
		this.array=array;
		this.name=name;
		this.idValue=idValue;
		this.config=config;
		this.datas=datas;
	}

	public ResourceEntry(String type,String name,int idValue,String config,String[] datas) {
		this(false, type, name, idValue, config, datas);
	}

	public boolean equals(Object object) {
		if(!(object instanceof ResourceEntry)){
			return false;
		}
		ResourceEntry that=(ResourceEntry)object;
		return ObjectUtil.equal(this.array, that.array) && ObjectUtil.equal(this.rType, that.rType) && ObjectUtil.equal(this.name, that.name) && ObjectUtil.equal(this.idValue, that.idValue);
	}

	public int hashCode() {
		return Arrays.hashCode(new Object[]{this.array, this.rType, this.name, this.idValue});
	}

	public int compareTo(ResourceEntry o) {
		return this.toRTxtEntryString().compareTo(o.toRTxtEntryString());
	}

	public String toString() {
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(toRTxtEntryString());
		stringBuilder.append(StringUtil.SPACE);
		stringBuilder.append(getValue());
		return stringBuilder.toString();
	}

	public String toRTxtEntryString(){
		return toRTxtEntryString(true);
	}

	private String toRTxtEntryString(boolean printIdValue){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(this.array?"int[]":"int");
		stringBuilder.append(StringUtil.SPACE);
		stringBuilder.append(this.rType);
		stringBuilder.append(StringUtil.SPACE);
		stringBuilder.append(this.name);
		if(printIdValue){
			stringBuilder.append(StringUtil.SPACE);
			stringBuilder.append("0x"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(this.idValue)));
		}
		return stringBuilder.toString();
	}

	private String getValue(){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(this.config);
		if(this.datas!=null){
			stringBuilder.append(StringUtil.SPACE);
			int i=0;
			for(String data:this.datas){
				stringBuilder.append(data);
				if(i<this.datas.length-1){
					stringBuilder.append(StringUtil.SPACE);
				}
				i++;
			}
		}
		return stringBuilder.toString();
	}

	public String toNoIdValueString(){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(toRTxtEntryString(false));
		stringBuilder.append(StringUtil.SPACE);
		stringBuilder.append(getValue());
		return stringBuilder.toString();
	}

	public String toKey(){
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(toRTxtEntryString(false));
		stringBuilder.append(StringUtil.SPACE);
		stringBuilder.append(this.config);
		return stringBuilder.toString();
	}
}
