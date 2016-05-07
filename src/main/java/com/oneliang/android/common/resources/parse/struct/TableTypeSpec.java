package com.oneliang.android.common.resources.parse.struct;

import java.util.ArrayList;
import java.util.List;

public class TableTypeSpec {

	public Header header=new Header();
	public byte resourceTypeId=1;
	public byte resource0=0;
	public short resource1=0;
	public int configCount=0;
	public List<TableType> tableTypeList=new ArrayList<TableType>();
}
