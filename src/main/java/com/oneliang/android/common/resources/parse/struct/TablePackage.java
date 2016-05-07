package com.oneliang.android.common.resources.parse.struct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TablePackage {

	public Header header=new Header();
	public int packageId=0;
	public String packageName=null;
	public int resourceTypeOffset=0;
	public int lastPublicType=0;
	public int resourceKeywordOffset=0;
	public int lastPublicKey=0;
	public final Set<ResourceEntry> noRepeatResourceEntrySet=new HashSet<ResourceEntry>();
	public final List<ResourceEntry> allResourceEntryList=new ArrayList<ResourceEntry>();
}
