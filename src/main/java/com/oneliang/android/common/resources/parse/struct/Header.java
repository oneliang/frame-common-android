package com.oneliang.android.common.resources.parse.struct;

public class Header {

	public final static short TYPE_NONE = -1;
	public final static short TYPE_TABLE = 0x0002;
	public final static short TYPE_PACKAGE = 0x0200;
	public final static short TYPE_TYPE = 0x0201;
	public final static short TYPE_SPEC_TYPE = 0x0202;
	public final static short TYPE_LIBRARY = 0x0203;

	public short type = 0;
	public short size = 0;
	public int chunkSize = 0;
}
