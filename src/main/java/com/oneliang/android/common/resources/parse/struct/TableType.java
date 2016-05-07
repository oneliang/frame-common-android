package com.oneliang.android.common.resources.parse.struct;

import com.oneliang.android.common.resources.data.ResConfigFlags;

public class TableType {

	public Header header=new Header();
	public byte resourceTypeId=1;
	public byte resource0=0;
	public short resource1=0;
	public int entryCount=0;
	public int entryStart=0;
	public int configSize=0;
	public int[] entryOffsetArray=null;
	public ResConfigFlags resConfigFlags=null;
}
