package com.oneliang.android.common.resources.parse.block;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.Constant;
import com.oneliang.android.common.resources.parse.struct.ResourceEntry;
import com.oneliang.android.common.resources.parse.struct.Table;
import com.oneliang.frame.io.CountingInputStream;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public class ArscBlock extends ComplexBlock {

    private CountingInputStream countingInputStream = null;

    // id
    private static final int ID_HEADER = 1;
    private static final int ID_PACKAGE_COUNT = 2;
    private static final int ID_STRING_POOL = 3;
    private static final int ID_TABLE_PACKAGE = 4;
    // struct
    private final Table table = new Table();
    // block
    private HeaderBlock headerBlock = new HeaderBlock();
    private Block packageCountBlock = new UnitBlock(Endian.LITTLE, 4);
    final StringPoolBlock globalStringPoolBlock = new StringPoolBlock();
    private List<TablePackageBlock> tablePackageBlockList = null;

    private Queue<BlockWrapper> blockWrapperQueue = new ConcurrentLinkedQueue<BlockWrapper>();

    {
        // parse block queue
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER, this.headerBlock));// header
        this.blockWrapperQueue.add(new BlockWrapper(ID_PACKAGE_COUNT, packageCountBlock));// header,package
                                                                                          // count,header
                                                                                          // count
                                                                                          // 12byte
        this.blockWrapperQueue.add(new BlockWrapper(ID_STRING_POOL, this.globalStringPoolBlock));// resource
                                                                                                 // string
                                                                                                 // pool,style
                                                                                                 // content
    }

    public void parse(InputStream inputStream) throws Exception {
        if (this.countingInputStream == null && inputStream != null && inputStream instanceof CountingInputStream) {
            this.countingInputStream = (CountingInputStream) inputStream;
        }
        super.parse(inputStream);
    }

    protected void beforeRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            Logger.log("----------resource arsc----------");
            break;
        }
    }

    protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            this.table.header = headerBlock.header;
            break;
        case ID_PACKAGE_COUNT:
            this.table.packageCount = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("packageCount:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + table.packageCount);
            this.tablePackageBlockList = new ArrayList<TablePackageBlock>(this.table.packageCount);
            for (int i = 0; i < this.table.packageCount; i++) {
                TablePackageBlock tablePackageBlock = new TablePackageBlock(this);
                this.blockWrapperQueue.add(new BlockWrapper(ID_TABLE_PACKAGE, tablePackageBlock));
                this.tablePackageBlockList.add(tablePackageBlock);
            }
            break;
        }
        super.afterRead(currentIndex, currentId, currentBlock);
    }

    protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
        return this.blockWrapperQueue;
    }

    public void writeRTxt(String outputDirectory) {
        String outputFullFilename = new File(outputDirectory).getAbsolutePath() + Constant.Symbol.SLASH_LEFT + "R" + Constant.Symbol.DOT + Constant.File.TXT;
        FileUtil.createFile(outputFullFilename);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(outputFullFilename));
            if (this.tablePackageBlockList != null) {
                for (TablePackageBlock tablePackageBlock : this.tablePackageBlockList) {
                    for (ResourceEntry resourceEntry : tablePackageBlock.tablePackage.noRepeatResourceEntrySet) {
                        writer.println(resourceEntry);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    public void print() {
        for (int i = 0; i < this.globalStringPoolBlock.stringPool.stringCount; i++) {
            Logger.log("global string:" + this.globalStringPoolBlock.getString(i));
        }
        for (int i = 0; i < this.globalStringPoolBlock.stringPool.styleCount; i++) {
            int[] styleIntArray = this.globalStringPoolBlock.getStyle(i);
            if (styleIntArray != null) {
                Logger.log("global style:" + styleIntArray.length);
                for (int styleInt : styleIntArray) {
                    Logger.log("\tint:" + styleInt + "," + this.globalStringPoolBlock.getString(styleInt));
                }
            }
        }
        if (this.tablePackageBlockList != null) {
            for (TablePackageBlock tablePackageBlock : this.tablePackageBlockList) {
                for (int i = 0; i < tablePackageBlock.resourceKeywordStringPoolBlock.stringPool.stringCount; i++) {
                    Logger.log("keyword:" + tablePackageBlock.resourceKeywordStringPoolBlock.getString(i));
                }
                for (int i = 0; i < tablePackageBlock.resourceTypeStringPoolBlock.stringPool.stringCount; i++) {
                    Logger.log("resource type:" + tablePackageBlock.resourceTypeStringPoolBlock.getString(i));
                }

                TreeSet<ResourceEntry> noRepeatTreeSet = new TreeSet<ResourceEntry>(tablePackageBlock.tablePackage.noRepeatResourceEntrySet);
                for (ResourceEntry resourceEntry : noRepeatTreeSet) {
                    Logger.log(resourceEntry);
                }
                Logger.log("----------");
                // TreeSet<ResourceEntry> allResourceTreeSet=new
                // TreeSet<ResourceEntry>(tablePackageBlock.tablePackage.allResourceEntryList);
                for (ResourceEntry resourceEntry : tablePackageBlock.tablePackage.allResourceEntryList) {
                    Logger.log(resourceEntry.toNoIdValueString());
                }
            }
        }
        // List<String>
        // resourceTypeStringList=this.resourceTypeStringPoolBlock.stringPool.stringList;
        // for(String resourceType:resourceTypeStringList){
        // Logger.log("resource type:"+resourceType);
        // }
        // List<String>
        // keywordStringList=this.resourceKeywordStringPoolBlock.stringPool.stringList;
        // Logger.log("keyword string list size:"+keywordStringList.size());
        // for(TableTypeSpecBlock
        // tableTypeSpecBlock:TableTypeSpecBlock.tableTypeSpecBlockList){
        // for(TableType
        // tableType:tableTypeSpecBlock.tableTypeSpec.tableTypeList){
        // int i=0;
        // for(Entry entry:tableType.entryList){
        // Logger.log(resourceTypeStringList.get(tableType.resourceTypeId-1)+"
        // "+keywordStringList.get(entry.index)+"
        // "+StringUtil.byteToHexString(MathUtil.intToByteArray(0x7F000000|(tableType.resourceTypeId<<0x10)|tableType.entryOrderList.get(i))));
        // i++;
        // }
        // }
        // }
    }

    public String getString(int resourceId) {
        String result = null;
        byte[] intByteArray = MathUtil.intToByteArray(resourceId);
        if (intByteArray.length == 4) {
            int packageId = intByteArray[0];
            int typeId = intByteArray[1];
            short order = MathUtil.byteArrayToShort(new byte[] { intByteArray[2], intByteArray[3] });
            // TableTypeSpecBlock
            // tableTypeSpecBlock=TableTypeSpecBlock.tableTypeSpecBlockList.get(typeId-1);
            // Entry
            // entry=tableTypeSpecBlock.tableTypeSpec.tableTypeList.get(0).entryList.get(order);
            // result=this.resourceKeywordStringPoolBlock.stringPool.stringList.get(entry.index)+","+this.globalStringPoolBlock.stringPool.stringList.get(entry.value);
        }
        return result;
    }

    public String getStringByOffset(int resourceId, String file) throws Exception {
        String result = null;
        // byte[] intByteArray=MathUtil.intToByteArray(resourceId);
        // RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
        // if(intByteArray.length==4){
        // int packageId=intByteArray[0];
        // int typeId=intByteArray[1];
        // short order=MathUtil.byteArrayToShort(new
        // byte[]{intByteArray[2],intByteArray[3]});
        //// TableTypeSpecBlock
        // tableTypeSpecBlock=TableTypeSpecBlock.tableTypeSpecBlockList.get(typeId-1);
        // Entry
        // entry=null;//tableTypeSpecBlock.tableTypeSpec.tableTypeList.get(0).entryList.get(order);
        //// entry.value
        // int offset=this.headerBlock.getTotalSize()+4;
        // int
        // stringOffset=this.globalStringPoolBlock.stringPool.stringOffsetArray[entry.value];
        // int nextStringOffset=0;
        // if(entry.value==(this.globalStringPoolBlock.stringPool.stringCount-1)){
        // if(this.globalStringPoolBlock.stringPool.stylesStart==0){
        // nextStringOffset=this.globalStringPoolBlock.stringPool.header.chunkSize-this.globalStringPoolBlock.stringPool.stringCount*4-28-3;//28
        // is string pool header size,3 is string pool end
        // }else{
        // nextStringOffset=this.globalStringPoolBlock.stringPool.stylesStart;
        // }
        // }else{
        // nextStringOffset=this.globalStringPoolBlock.stringPool.stringOffsetArray[entry.value+1];
        // }
        // Logger.log("stringsStart:"+this.globalStringPoolBlock.stringPool.stringsStart);
        // Logger.log("offset:"+offset+",stringOffset:"+stringOffset+",nextStringOffset:"+nextStringOffset);
        // Logger.log("offset:"+offset+",stringOffset:"+(stringOffset+offset)+",nextStringOffset:"+(nextStringOffset+offset));
        // randomAccessFile.seek(stringOffset+offset+this.globalStringPoolBlock.stringPool.stringsStart);
        // byte[] buffer=new byte[nextStringOffset-stringOffset];
        // int length=randomAccessFile.read(buffer, 0, buffer.length);
        // if(buffer.length==length){
        // ByteArrayInputStream byteArrayInputStream=new
        // ByteArrayInputStream(buffer);
        // byteArrayInputStream.read();
        // byteArrayInputStream.read();
        // buffer=new byte[nextStringOffset-stringOffset-3];
        // byteArrayInputStream.read(buffer, 0, buffer.length);
        // result=new String(buffer,Constant.Encoding.UTF8);
        // }
        // }
        // randomAccessFile.close();
        return result;
    }

    /**
     * @return the countingInputStream
     */
    public CountingInputStream getCountingInputStream() {
        return countingInputStream;
    }

    /**
     * @return the tablePackageBlockList
     */
    public List<TablePackageBlock> getTablePackageBlockList() {
        return tablePackageBlockList;
    }

    /**
     * @return the headerBlock
     */
    public HeaderBlock getHeaderBlock() {
        return headerBlock;
    }

    /**
     * @return the globalStringPoolBlock
     */
    public StringPoolBlock getGlobalStringPoolBlock() {
        return globalStringPoolBlock;
    }

    /**
     * @return the packageCountBlock
     */
    public Block getPackageCountBlock() {
        return packageCountBlock;
    }
}
