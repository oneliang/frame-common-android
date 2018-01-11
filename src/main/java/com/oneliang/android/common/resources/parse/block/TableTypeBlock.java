package com.oneliang.android.common.resources.parse.block;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.android.common.resources.data.ResConfigFlags;
import com.oneliang.android.common.resources.parse.struct.Header;
import com.oneliang.android.common.resources.parse.struct.ResourceEntry;
import com.oneliang.android.common.resources.parse.struct.TableType;
import com.oneliang.android.common.resources.util.TypedValue;
import com.oneliang.frame.io.LEDataInputStream;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;

public class TableTypeBlock extends ComplexBlock {

	private static final int ID_HEADER=1;
	private static final int ID_RESOURCE_TYPE_ID=2;
	private static final int ID_RESOURCE0=3;
	private static final int ID_RESOURCE1=4;
	private static final int ID_ENTRY_COUNT=5;
	private static final int ID_ENTRY_START=6;
	private static final int ID_CONFIG_SIZE=7;
	private static final int ID_CONFIG=8;
	private static final int ID_ENTRY_OFFSET_ARRAY=9;
	private static final int ID_ENTRY=10;

	private static final int KNOWN_CONFIG_BYTES = 52;
	private static final short ENTRY_FLAG_COMPLEX = 0x0001;
	private static final short ENTRY_FLAG_PUBLIC = 0x0002;
	private static final short ENTRY_FLAG_WEAK = 0x0004;

	//reference
	private ArscBlock arscBlock=null;
	private TablePackageBlock tablePackageBlock=null;

	//struct
	final TableType tableType=new TableType();
	private boolean[] missingResSpecArray = null;

	//block
	private HeaderBlock headerBlock=new HeaderBlock();
	private Block resourceTypeIdBlock=new UnitBlock(Endian.LITTLE,1);
	private Block resource0Block=new UnitBlock(Endian.LITTLE,1);
	private Block resource1Block=new UnitBlock(Endian.LITTLE,2);
	private Block entryCountBlock=new UnitBlock(Endian.LITTLE,4);
	private Block entryStartBlock=new UnitBlock(Endian.LITTLE,4);
	private Block configSizeBlock=new UnitBlock(Endian.LITTLE,4);
	private Block configBlock=new UnitBlock(0);
	private Block entryOffsetArray=new UnitBlock(0);
	private Block entryBlock=new UnitBlock(0);

	private Queue<BlockWrapper> blockWrapperQueue=new ConcurrentLinkedQueue<BlockWrapper>();

	{
		this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER,this.headerBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_TYPE_ID,this.resourceTypeIdBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE0,this.resource0Block));
		this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE1,this.resource1Block));
		this.blockWrapperQueue.add(new BlockWrapper(ID_ENTRY_COUNT,this.entryCountBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_ENTRY_START,this.entryStartBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_CONFIG_SIZE,this.configSizeBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_CONFIG,this.configBlock));
		this.blockWrapperQueue.add(new BlockWrapper(ID_ENTRY_OFFSET_ARRAY,this.entryOffsetArray));
		this.blockWrapperQueue.add(new BlockWrapper(ID_ENTRY,this.entryBlock));
	}

	public TableTypeBlock(ArscBlock arscBlock,TablePackageBlock tablePackageBlock) {
		super();
		this.arscBlock=arscBlock;
		this.tablePackageBlock=tablePackageBlock;
	}

	public TableTypeBlock(ArscBlock arscBlock,TablePackageBlock tablePackageBlock,byte[] beforeByteArray){
		super(beforeByteArray);
		this.arscBlock=arscBlock;
		this.tablePackageBlock=tablePackageBlock;
	}

	protected void beforeRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
		switch(currentId){
		case ID_HEADER:
			Logger.log("----------resource table type----------");
			break;
		case ID_CONFIG:
			currentBlock.setInitialSize(this.tableType.configSize-4);
			break;
		case ID_ENTRY_OFFSET_ARRAY:
			currentBlock.setInitialSize(tableType.entryCount*4);
			break;
		case ID_ENTRY:
			currentBlock.setInitialSize(this.tableType.header.chunkSize-this.tableType.entryStart);
			break;
		}
	}

	protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
		switch(currentId){
		case ID_HEADER:
			this.tableType.header=headerBlock.header;
			if(this.tableType.header.type!=Header.TYPE_TYPE){
				throw new RuntimeException("header type error,excepted:"+StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(Header.TYPE_TYPE))+",current:"+StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(this.tableType.header.type)));
			}
			break;
		case ID_RESOURCE_TYPE_ID:
			this.tableType.resourceTypeId=currentBlock.getValue()[0];
			Logger.log("resourceTypeId:0x"+StringUtil.byteArrayToHexString(currentBlock.getValue())+","+this.tableType.resourceTypeId);
			break;
		case ID_RESOURCE0:
			this.tableType.resource0=currentBlock.getValue()[0];
			Logger.log("resource0:0x"+StringUtil.byteArrayToHexString(currentBlock.getValue())+","+this.tableType.resource0);
			break;
		case ID_RESOURCE1:
			this.tableType.resource1=MathUtil.byteArrayToShort(currentBlock.getValue());
			Logger.log("resource1:0x"+StringUtil.byteArrayToHexString(currentBlock.getValue())+","+this.tableType.resource1);
			break;
		case ID_ENTRY_COUNT:
			this.tableType.entryCount=MathUtil.byteArrayToInt(currentBlock.getValue());
			this.missingResSpecArray = new boolean[this.tableType.entryCount];
			Arrays.fill(this.missingResSpecArray, true);
			Logger.log("entryCount:0x"+StringUtil.byteArrayToHexString(currentBlock.getValue())+","+this.tableType.entryCount);
			break;
		case ID_ENTRY_START:
			this.tableType.entryStart=MathUtil.byteArrayToInt(currentBlock.getValue());
			Logger.log("entryStart:0x"+StringUtil.byteArrayToHexString(currentBlock.getValue())+","+this.tableType.entryStart);
			break;
		case ID_CONFIG_SIZE:
			this.tableType.configSize=MathUtil.byteArrayToInt(currentBlock.getValue());
			Logger.log("configSize:"+this.tableType.configSize);
			break;
		case ID_CONFIG:
			ResConfigFlags resConfigFlags=this.readConfigFlags(this.tableType.configSize, currentBlock.getValue());
			this.tableType.resConfigFlags=resConfigFlags;
			Logger.log(resConfigFlags);
			break;
		case ID_ENTRY_OFFSET_ARRAY:{
			byte[] byteArray=currentBlock.getValue();
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
			this.tableType.entryOffsetArray=new int[this.tableType.entryCount];
			for(int i=0;i<this.tableType.entryCount;i++){
				byte[] buffer=new byte[4];
				byteArrayInputStream.read(buffer);
				int offset=MathUtil.byteArrayToInt(MathUtil.byteArrayReverse(buffer));
				this.tableType.entryOffsetArray[i]=offset;
			}
			Logger.log("entry offset array,typeId:"+this.tableType.resourceTypeId+"$"+this.tablePackageBlock.resourceTypeStringPoolBlock.getString(this.tableType.resourceTypeId-1)+",byte count:"+currentBlock.getTotalSize()+",original byte:"+StringUtil.byteArrayToHexString(currentBlock.getValue()));
			break;
		}
		case ID_ENTRY:
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(currentBlock.getValue());
			LEDataInputStream leDataInputStream=new LEDataInputStream(byteArrayInputStream);
			byte typeId=this.tableType.resourceTypeId;
			int packageId=this.tablePackageBlock.tablePackage.packageId;
			for (int i = 0; i < this.tableType.entryOffsetArray.length; i++) {
				if (this.tableType.entryOffsetArray[i] != -1) {
					this.missingResSpecArray[i] = false;
					int resId = ((packageId << 24 | typeId << 16) & 0xffff0000) | i;
					ResourceEntry resourceEntry=readEntry(leDataInputStream, resId);
					this.tablePackageBlock.tablePackage.noRepeatResourceEntrySet.add(resourceEntry);
					this.tablePackageBlock.tablePackage.allResourceEntryList.add(resourceEntry);
				}
			}
			break;
		}
		super.afterRead(currentIndex, currentId, currentBlock);
	}

	protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
		return this.blockWrapperQueue;
	}

	private ResConfigFlags readConfigFlags(int size, byte[] byteArray) throws Exception{
		ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
		LEDataInputStream leDataInputStream=new LEDataInputStream(byteArrayInputStream);
		int read = 28;

		if (size < 28) {
			throw new RuntimeException("Config size < 28");
		}

		boolean isInvalid = false;

		short mcc = leDataInputStream.readShort();
		short mnc = leDataInputStream.readShort();

		char[] language = this.unpackLanguageOrRegion(leDataInputStream.readByte(), leDataInputStream.readByte(), 'a');
		char[] country = this.unpackLanguageOrRegion(leDataInputStream.readByte(), leDataInputStream.readByte(), '0');
		byte orientation = leDataInputStream.readByte();
		byte touchscreen = leDataInputStream.readByte();

		int density = leDataInputStream.readUnsignedShort();

		byte keyboard = leDataInputStream.readByte();
		byte navigation = leDataInputStream.readByte();
		byte inputFlags = leDataInputStream.readByte();
		/* inputPad0 */leDataInputStream.skipBytes(1);

		short screenWidth = leDataInputStream.readShort();
		short screenHeight = leDataInputStream.readShort();

		short sdkVersion = leDataInputStream.readShort();
		/* minorVersion, now must always be 0 */leDataInputStream.skipBytes(2);

		byte screenLayout = 0;
		byte uiMode = 0;
		short smallestScreenWidthDp = 0;
		if (size >= 32) {
			screenLayout = leDataInputStream.readByte();
			uiMode = leDataInputStream.readByte();
			smallestScreenWidthDp = leDataInputStream.readShort();
			read = 32;
		}

		short screenWidthDp = 0;
		short screenHeightDp = 0;
		if (size >= 36) {
			screenWidthDp = leDataInputStream.readShort();
			screenHeightDp = leDataInputStream.readShort();
			read = 36;
		}

		char[] localeScript = null;
		char[] localeVariant = null;
		if (size >= 48) {
			localeScript = readScriptOrVariantChar(leDataInputStream, 4).toCharArray();
			localeVariant = readScriptOrVariantChar(leDataInputStream, 8).toCharArray();
			read = 48;
		}

		byte screenLayout2 = 0;
		if (size >= 52) {
			screenLayout2 = leDataInputStream.readByte();
			leDataInputStream.skipBytes(3); // reserved padding
			read = 52;
		}

		int exceedingSize = size - KNOWN_CONFIG_BYTES;
		if (exceedingSize > 0) {
			byte[] buf = new byte[exceedingSize];
			read += exceedingSize;
			leDataInputStream.readFully(buf);
			BigInteger exceedingBI = new BigInteger(1, buf);

			if (exceedingBI.equals(BigInteger.ZERO)) {
				Logger.log(String.format("Config flags size > %d, but exceeding bytes are all zero, so it should be ok.", KNOWN_CONFIG_BYTES));
			} else {
				Logger.log(String.format("Config flags size > %d. Exceeding bytes: 0x%X.", KNOWN_CONFIG_BYTES, exceedingBI));
				isInvalid = true;
			}
		}

		int remainingSize = size - read;
		if (remainingSize > 0) {
			leDataInputStream.skipBytes(remainingSize);
		}

		return new ResConfigFlags(mcc, mnc, language, country, orientation, touchscreen, density, keyboard, navigation, inputFlags, screenWidth, screenHeight, sdkVersion, screenLayout, uiMode, smallestScreenWidthDp, screenWidthDp, screenHeightDp, localeScript, localeVariant, screenLayout2, isInvalid, size);
	}

	private char[] unpackLanguageOrRegion(byte in0, byte in1, char base){
		// check high bit, if so we have a packed 3 letter code
		if (((in0 >> 7) & 1) == 1) {
			int first = in1 & 0x1F;
			int second = ((in1 & 0xE0) >> 5) + ((in0 & 0x03) << 3);
			int third = (in0 & 0x7C) >> 2;

			// since this function handles languages & regions, we add the
			// value(s) to the base char
			// which is usually 'a' or '0' depending on language or region.
			return new char[] { (char) (first + base), (char) (second + base), (char) (third + base) };
		}
		return new char[] { (char) in0, (char) in1 };
	}

	private String readScriptOrVariantChar(LEDataInputStream leDataInputStream, int length) throws IOException {
		StringBuilder string = new StringBuilder(16);

		while (length-- != 0) {
			short ch = leDataInputStream.readByte();
			if (ch == 0) {
				break;
			}
			string.append((char) ch);
		}
		leDataInputStream.skipBytes(length);

		return string.toString();
	}

	private ResourceEntry readEntry(LEDataInputStream leDataInputStream, int resId) throws IOException {
		int size=leDataInputStream.readShort();
		short flag = leDataInputStream.readShort();
		int specNameId = leDataInputStream.readInt();
		String resourceType=this.tablePackageBlock.resourceTypeStringPoolBlock.getString(this.tableType.resourceTypeId-1);
		String resourceName=this.tablePackageBlock.resourceKeywordStringPoolBlock.getString(specNameId);
		Logger.log("entry size:"+size+",flag:"+StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(flag))+",spec name id:"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(specNameId))+",spec name:"+this.tablePackageBlock.resourceKeywordStringPoolBlock.getString(specNameId)+",res id:0x"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(resId)));
		String[] values=null;
		if((flag & ENTRY_FLAG_COMPLEX) == 0 ){
			String value=readValue(leDataInputStream);
			values=new String[]{value};
		}else{
			values=readComplexEntry(leDataInputStream);
		}
		ResourceEntry resourceEntry=new ResourceEntry(resourceType,resourceName,resId,this.tableType.resConfigFlags.toString(), values);
		return resourceEntry;
	}

	private String readValue(LEDataInputStream leDataInputStream) throws IOException {
		/* size */leDataInputStream.readShort();
		/* zero */leDataInputStream.readByte();
		byte type = leDataInputStream.readByte();
		int data = leDataInputStream.readInt();
		Logger.log("original.type:"+type+",data index:"+data);
		String value=null;
		if(type == TypedValue.TYPE_STRING){
			value=this.arscBlock.globalStringPoolBlock.getHTML(data);
			Logger.log("TYPE_STRING:"+value);
		}else{
			value=transformRealValue(type, data, null);
		}
		return value;
//		return type == TypedValue.TYPE_STRING ? mPkg.getValueFactory().factory(mTableStrings.getHTML(data), data) : mPkg.getValueFactory().factory(type, data, null);
	}

	private String[] readComplexEntry(LEDataInputStream leDataInputStream) throws IOException {
		int parent = leDataInputStream.readInt();
		int count = leDataInputStream.readInt();
		Logger.log("\tcomplex entry begin,count:"+count);
		int resId=0;
		String[] values=new String[count];
		for (int i = 0; i < count; i++) {
			resId = leDataInputStream.readInt();
			String value=readValue(leDataInputStream);
			values[i]=value;
			Logger.log("parent:"+parent+",parentHex:0x"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(parent))+",res id:0x"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(resId)));
		}
		Logger.log("\tcomplex entry end");
		return values;
	}

	private String transformRealValue(int type, int value, String rawValue) {
		String dataValue=null;
		switch(type){
		case TypedValue.TYPE_REFERENCE:
			dataValue="@"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(value));
			break;
		default:
			dataValue=TypedValue.coerceToString(type, value);
			break;
		}
		Logger.log("before switch type:"+type+",data:"+value+",dataHex:#"+StringUtil.byteArrayToHexString(MathUtil.intToByteArray(value))+",dataValue:"+dataValue);
//		String dataValue=null;
//		switch (type) {
//		case TypedValue.TYPE_NULL:
//			if (value == TypedValue.DATA_NULL_EMPTY) { // Special case $empty as explicitly defined empty value
//			//	                    return new ResStringValue(null, value);
//				dataValue=null;
//			}
//			//	                return new ResReferenceValue(mPackage, 0, null);
//			break;
//		case TypedValue.TYPE_REFERENCE:
//			//	                return newReference(value, rawValue);
//			Logger.log("type:"+type+",data:"+value+",value:#"+StringUtil.byteToHexString(MathUtil.intToByteArray(value)));
//			dataValue=String.valueOf(value);
//			break;
//		case TypedValue.TYPE_ATTRIBUTE:
//			//	                return newReference(value, rawValue, true);
//			break;
//		case TypedValue.TYPE_STRING:
//			//	                return new ResStringValue(rawValue, value);
//			dataValue=String.valueOf(value);
//			break;
//		case TypedValue.TYPE_FLOAT:
//			//	                return new ResFloatValue(Float.intBitsToFloat(value), value, rawValue);
//			break;
//		case TypedValue.TYPE_DIMENSION:
//			//	                return new ResDimenValue(value, rawValue);
//			Logger.log("type:"+type+",data:"+value+","+TypedValue.coerceToString(type, value));
//			dataValue=TypedValue.coerceToString(type, value);
//			break;
//		case TypedValue.TYPE_FRACTION:
//			//	                return new ResFractionValue(value, rawValue);
//			break;
//		case TypedValue.TYPE_INT_BOOLEAN:
//			Logger.log("type:"+type+",data:"+value+",value:"+(value!=0));
//			dataValue=String.valueOf(value!=0);
//			break;
//		case TypedValue.TYPE_DYNAMIC_REFERENCE:
//			//	                return newReference(value, rawValue);
//			break;
//		}
//		if(dataValue==null){
//			if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
//				Logger.log("type:"+type+",data:"+value+",value:#"+TypedValue.coerceToString(type, value));
//				//	            return new ResColorValue(value, rawValue);
//				dataValue=TypedValue.coerceToString(type, value);
//			}else if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
//				//	            return new ResIntValue(value, rawValue, type);
//				Logger.log("type:"+type+",data:"+value+",value:"+value);
//				dataValue=String.valueOf(value);
//			}
//		}
		return dataValue;
	}

	public List<Section> toSectionList(){
		List<Section> sectionList=new ArrayList<Section>();
		sectionList.add(this.headerBlock);
		sectionList.add(this.resourceTypeIdBlock);
		sectionList.add(this.resource0Block);
		sectionList.add(this.resource1Block);
		sectionList.add(this.entryCountBlock);
		sectionList.add(this.entryStartBlock);
		sectionList.add(this.configSizeBlock);
		sectionList.add(this.configBlock);
		sectionList.add(this.entryOffsetArray);
		sectionList.add(this.entryBlock);
		return sectionList;
	}
}
