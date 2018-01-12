package com.oneliang.android.common.resources.parse.block;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.android.common.resources.parse.struct.Header;
import com.oneliang.android.common.resources.parse.struct.TableTypeSpec;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;

public class TableTypeSpecBlock extends ComplexBlock {

    private static final int ID_HEADER = 1;
    private static final int ID_RESOURCE_TYPE_ID = 2;
    private static final int ID_RESOURCE0 = 3;
    private static final int ID_RESOURCE1 = 4;
    private static final int ID_CONFIG_COUNT = 5;
    private static final int ID_HEADER_REAR = 6;
    private static final int ID_CONFIG_ARRAY = 7;
    private static final int ID_NEXT_HEADER = 8;
    private static final int ID_TABLE_TYPE_BLOCK = 9;
    private static final int ID_TABLE_TYPE_SPEC_BLOCK = 10;

    private static final int GENERAL_BLOCK_SIZE = 16;// must be check
    // reference
    private ArscBlock arscBlock = null;
    private TablePackageBlock tablePackageBlock = null;

    // struct
    public final TableTypeSpec tableTypeSpec = new TableTypeSpec();

    // block
    private HeaderBlock headerBlock = new HeaderBlock();
    private Block resourceTypeIdBlock = new UnitBlock(Endian.LITTLE, 1);
    private Block resource0Block = new UnitBlock(Endian.LITTLE, 1);
    private Block resource1Block = new UnitBlock(Endian.LITTLE, 2);
    private Block configCountBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block headerRearBlock = new UnitBlock(0);
    private Block configArrayBlock = new UnitBlock(0);

    final List<TableTypeBlock> tableTypeBlockList = new ArrayList<TableTypeBlock>();

    private Queue<BlockWrapper> blockWrapperQueue = new ConcurrentLinkedQueue<BlockWrapper>();

    {
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER, this.headerBlock));// header
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE_TYPE_ID, this.resourceTypeIdBlock));
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE0, this.resource0Block));
        this.blockWrapperQueue.add(new BlockWrapper(ID_RESOURCE1, this.resource1Block));
        this.blockWrapperQueue.add(new BlockWrapper(ID_CONFIG_COUNT, this.configCountBlock));
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER_REAR, this.headerRearBlock));
        this.blockWrapperQueue.add(new BlockWrapper(ID_CONFIG_ARRAY, this.configArrayBlock));
    }

    public TableTypeSpecBlock(ArscBlock arscBlock, TablePackageBlock tablePackageBlock) {
        super();
        this.arscBlock = arscBlock;
        this.tablePackageBlock = tablePackageBlock;
        this.tablePackageBlock.tableTypeSpecBlockList.add(this);
    }

    public TableTypeSpecBlock(ArscBlock arscBlock, TablePackageBlock tablePackageBlock, byte[] beforeByteArray) {
        super(beforeByteArray);
        this.arscBlock = arscBlock;
        this.tablePackageBlock = tablePackageBlock;
        this.tablePackageBlock.tableTypeSpecBlockList.add(this);
    }

    protected void beforeRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            Logger.log("----------resource table type spec----------");
            break;
        case ID_HEADER_REAR:
            currentBlock.setInitialSize(this.headerBlock.header.size - GENERAL_BLOCK_SIZE);
            break;
        case ID_CONFIG_ARRAY:
            currentBlock.setInitialSize(this.tableTypeSpec.configCount * 4);
            break;
        }
    }

    protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            this.tableTypeSpec.header = this.headerBlock.header;
            if (this.tableTypeSpec.header.type != Header.TYPE_SPEC_TYPE) {
                throw new RuntimeException("header type error,excepted:" + StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(Header.TYPE_SPEC_TYPE)) + ",current:" + StringUtil.byteArrayToHexString(MathUtil.shortToByteArray(this.tableTypeSpec.header.type)));
            }
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_RESOURCE_TYPE_ID:
            this.tableTypeSpec.resourceTypeId = currentBlock.getValue()[0];
            Logger.log("resourceTypeId:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.tableTypeSpec.resourceTypeId);
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_RESOURCE0:
            this.tableTypeSpec.resource0 = currentBlock.getValue()[0];
            Logger.log("resource0:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.tableTypeSpec.resource0);
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_RESOURCE1:
            this.tableTypeSpec.resource1 = MathUtil.byteArrayToShort(currentBlock.getValue());
            Logger.log("resource1:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.tableTypeSpec.resource1);
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_CONFIG_COUNT:
            this.tableTypeSpec.configCount = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("configCount:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.tableTypeSpec.configCount);

            if (this.headerBlock.header.size > GENERAL_BLOCK_SIZE) {
                Logger.log("It is not a GENERAL_BLOCK_SIZE:" + GENERAL_BLOCK_SIZE + ",header size:" + this.headerBlock.header.size);
            } else {
                Logger.log("It is a GENERAL_BLOCK_SIZE:" + GENERAL_BLOCK_SIZE);
            }
            this.blockWrapperQueue.add(new BlockWrapper(ID_NEXT_HEADER, new HeaderBlock()));
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_CONFIG_ARRAY:
            if (this.tableTypeSpec.configCount != 0) {
                byte[] byteArray = currentBlock.getValue();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                for (int i = 0; i < this.tableTypeSpec.configCount; i++) {
                    byte[] buffer = new byte[4];
                    byteArrayInputStream.read(buffer);
                    int config = MathUtil.byteArrayToInt(MathUtil.byteArrayReverse(buffer));
                    Logger.log(StringUtil.byteArrayToHexString(MathUtil.byteArrayReverse(buffer)));
                }
            }
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        case ID_NEXT_HEADER:
            if (currentBlock.getTotalSize() != 0) {
                if (currentBlock != null && currentBlock instanceof HeaderBlock && !currentBlock.equals(this.headerBlock)) {
                    HeaderBlock headerBlock = (HeaderBlock) currentBlock;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(MathUtil.byteArrayReverse(MathUtil.shortToByteArray(headerBlock.header.type)));
                    byteArrayOutputStream.write(MathUtil.byteArrayReverse(MathUtil.shortToByteArray(headerBlock.header.size)));
                    byteArrayOutputStream.write(MathUtil.byteArrayReverse(MathUtil.intToByteArray(headerBlock.header.chunkSize)));
                    if (headerBlock.header.type == Header.TYPE_TYPE) {
                        TableTypeBlock tableTypeBlock = new TableTypeBlock(this.arscBlock, this.tablePackageBlock, byteArrayOutputStream.toByteArray());
                        this.tableTypeBlockList.add(tableTypeBlock);
                        this.tableTypeSpec.tableTypeList.add(tableTypeBlock.tableType);
                        this.blockWrapperQueue.add(new BlockWrapper(ID_TABLE_TYPE_BLOCK, tableTypeBlock));
                        this.blockWrapperQueue.add(new BlockWrapper(ID_NEXT_HEADER, new HeaderBlock()));
                    } else if (headerBlock.header.type == Header.TYPE_SPEC_TYPE) {
                        this.blockWrapperQueue.add(new BlockWrapper(ID_TABLE_TYPE_SPEC_BLOCK, new TableTypeSpecBlock(this.arscBlock, this.tablePackageBlock, byteArrayOutputStream.toByteArray())));
                    }
                }
            }
            break;
        default:
            super.afterRead(currentIndex, currentId, currentBlock);
            break;
        }
    }

    protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
        return this.blockWrapperQueue;
    }

    public List<Section> toSectionList() {
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.add(this.headerBlock);
        sectionList.add(this.resourceTypeIdBlock);
        sectionList.add(this.resource0Block);
        sectionList.add(this.resource1Block);
        sectionList.add(this.configCountBlock);
        sectionList.add(this.headerRearBlock);
        sectionList.add(this.configArrayBlock);
        for (TableTypeBlock tableTypeBlock : this.tableTypeBlockList) {
            sectionList.addAll(tableTypeBlock.toSectionList());
        }
        return sectionList;
    }
}
