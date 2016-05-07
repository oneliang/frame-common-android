package com.oneliang.android.common.resources.parse.struct;

public class StringPool {

	public static final int CHUNK_STRINGPOOL_TYPE = 0x001C0001;
	public static final int CHUNK_NULL_TYPE = 0x00000000;
	public static final int UTF8_FLAG = 0x00000100;

	public Header header=new Header();
	public int stringCount=0;
	public int styleCount=0;
	public int flags=0;
	public boolean isUTF8=false;//(flags & UTF8_FLAG) != 0;
	public int stringsStart=0;
	public int stylesStart=0;
	public int[] stringOffsetArray=null;
	//stringOwns? copy from apktool
	public int[] stringOwns=null;
	public int[] styleOffsetArray=null;
	public byte[] allStringByteArray=null;
	public int[] styleArray=null;
}
