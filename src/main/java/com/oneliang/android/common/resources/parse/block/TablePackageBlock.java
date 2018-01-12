package com.oneliang.android.common.resources.parse.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.android.common.resources.parse.struct.Header;
import com.oneliang.android.common.resources.parse.struct.TablePackage;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;

public class TablePackageBlock extends ComplexBlock {

    private static final int ID_HEADER = 1;
    private static final int ID_PACKAGE_ID = 2;
    private static final int ID_PACKAGE_NAME = 3;
    private static final int ID_RESOURCE_TYPE_OFFSET = 4;
    private static final int ID_LAST_PUBLIC_TYPE = 5;
    private static final int ID_RESOURCE_KEYWORD_OFFSET = 6;
    private static final int ID_LAST_PUBLIC_KEY = 7;
    private static final int ID_HEADER_REAR = 8;
    private static final int ID_RESOURCE_TYPE_STRING_POOL = 9;
    private static final int ID_RESOURCE_KEYWORD_STRING_POOL = 10;
    private static final int ID_TABLE_TYPE_SPEC = 11;

    private static final int GENERAL_BLOCK_SIZE = 284;// some time 288 byte,so
                                                      // must be check

    private ArscBlock arscBlock = null;
    // struct
    final TablePackage tablePackage = new TablePackage();
    // block
    private HeaderBlock headerBlock = new HeaderBlock();
    private Block packageIdBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block packageNameBlock = new UnitBlock(256);
    private Block resourceTypeOffetBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block lastPublicTypeBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block resourceKeywordOffsetBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block lastPublicKeyBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block headerRearBlock = new UnitBlock(0);
    final StringPoolBlock resourceTypeStringPoolBlock = new StringPoolBlock();
    final StringPoolBlock resourceKeywordStringPoolBlock = new StringPoolBlock();

    // child reference
    final List<TableTypeSpecBlock> tableTypeSpecBlockList = new ArrayList<TableTypeSpecBlock>();

    // private TableTypeSpecBlock tableTypeSpecBlock=null;
    private Queue<BlockWrapper> blockWrapperQueue = new ConcurrentLinkedQueue<BlockWrapper>();
    {
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER, this.headerBlock));// package
                                                                                  // table,type
        this.blockWrapperQueue.add(new BlockWrapper(ID_PACKAGE_ID, this.packageIdBlock));// package
                                                                                         // table,package
                                                                                         // id
        this.blockWrapperQueue.add(new BlockWrapper(ID_PACKAGE_NAME, this.packageNameBlock));// package
                                                                                             // table,package
                                                                                             // name
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_TYPE_OFFSET, this.resourceTypeOffetBlock));// package
                                                                                                           // table,resource
                                                                                                           // type
                                                                                                           // offset
        this.blockWrapperQueue.add(new BlockWrapper(ID_LAST_PUBLIC_TYPE, this.lastPublicTypeBlock));// package
                                                                                                    // table,last
                                                                                                    // public
                                                                                                    // type
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_KEYWORD_OFFSET, this.resourceKeywordOffsetBlock));// package
                                                                                                                  // table,resource
                                                                                                                  // key
                                                                                                                  // word
                                                                                                                  // offset
        this.blockWrapperQueue.add(new BlockWrapper(ID_LAST_PUBLIC_KEY, this.lastPublicKeyBlock));// package
                                                                                                  // table,last
                                                                                                  // public
                                                                                                  // key,header
                                                                                                  // count
                                                                                                  // 284
                                                                                                  // byte
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER_REAR, this.headerRearBlock));
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_TYPE_STRING_POOL, this.resourceTypeStringPoolBlock));
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_KEYWORD_STRING_POOL, this.resourceKeywordStringPoolBlock));
    }

    public TablePackageBlock(ArscBlock arscBlock) {
        super();
        this.arscBlock = arscBlock;
        TableTypeSpecBlock tableTypeSpecBlock = new TableTypeSpecBlock(this.arscBlock, this);
        this.blockWrapperQueue.add(new BlockWrapper(ID_TABLE_TYPE_SPEC, tableTypeSpecBlock));
    }

    protected void beforeRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            Logger.log("----------resource table package----------");
            break;
        case ID_HEADER_REAR:
            currentBlock.setInitialSize(this.headerBlock.header.size - GENERAL_BLOCK_SIZE);
            break;
        }
    }

    protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            this.tablePackage.header = this.headerBlock.header;
            if (this.tablePackage.header.type != Header.TYPE_PACKAGE) {
                throw new RuntimeException("header type error,excepted:" + StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(Header.TYPE_PACKAGE)) + ",current:" + StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(this.tablePackage.header.type)));
            }
            break;
        case ID_PACKAGE_ID:
            this.tablePackage.packageId = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()));
            break;
        case ID_PACKAGE_NAME:
            this.tablePackage.packageName = this.readPackageName(currentBlock.getValue());
            Logger.log("package name:" + tablePackage.packageName);
            break;
        case ID_RESOURCE_TYPE_OFFSET:
            this.tablePackage.resourceTypeOffset = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()));
            break;
        case ID_LAST_PUBLIC_TYPE:
            this.tablePackage.lastPublicType = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()));
            break;
        case ID_RESOURCE_KEYWORD_OFFSET:
            this.tablePackage.resourceKeywordOffset = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()));
            break;
        case ID_LAST_PUBLIC_KEY:
            this.tablePackage.lastPublicKey = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()));
            if (this.headerBlock.header.size > GENERAL_BLOCK_SIZE) {
                Logger.log("It is not a GENERAL_BLOCK_SIZE:" + GENERAL_BLOCK_SIZE + ",header size:" + this.headerBlock.header.size);
            } else {
                Logger.log("It is a GENERAL_BLOCK_SIZE:" + GENERAL_BLOCK_SIZE);
            }
            break;
        }
        super.afterRead(currentIndex, currentId, currentBlock);
    }

    protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
        return this.blockWrapperQueue;
    }

    /**
     * read package name
     * 
     * @param byteArray
     * @return String
     */
    private String readPackageName(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < byteArray.length / 2; i++) {
            byte one = byteArray[i * 2];
            byte two = byteArray[i * 2 + 1];
            char character = (char) MathUtil.byteArrayToShort(new byte[] { two, one });
            if (character != 0) {
                stringBuilder.append(character);
            } else {
                break;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @return the tablePackage
     */
    public TablePackage getTablePackage() {
        return tablePackage;
    }

    public List<Section> toSectionList() {
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.add(this.headerBlock);
        sectionList.add(this.packageIdBlock);
        sectionList.add(this.packageNameBlock);
        sectionList.add(this.resourceTypeOffetBlock);
        sectionList.add(this.lastPublicTypeBlock);
        sectionList.add(this.resourceKeywordOffsetBlock);
        sectionList.add(this.lastPublicKeyBlock);
        sectionList.add(this.headerRearBlock);
        return sectionList;
    }

    /**
     * @return the resourceTypeStringPoolBlock
     */
    public StringPoolBlock getResourceTypeStringPoolBlock() {
        return resourceTypeStringPoolBlock;
    }

    /**
     * @return the resourceKeywordStringPoolBlock
     */
    public StringPoolBlock getResourceKeywordStringPoolBlock() {
        return resourceKeywordStringPoolBlock;
    }

    /**
     * @return the tableTypeSpecBlockList
     */
    public List<TableTypeSpecBlock> getTableTypeSpecBlockList() {
        return tableTypeSpecBlockList;
    }
}
