package com.oneliang.android.common.resources.parse.block;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.android.common.resources.parse.struct.Header;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;

public class HeaderBlock extends ComplexBlock {

    private static final int ID_TYPE = 1;
    private static final int ID_HEADER_SIZE = 2;
    private static final int ID_BLOCK_SIZE = 3;

    final Header header = new Header();
    private Queue<BlockWrapper> blockWrapperQueue = new ConcurrentLinkedQueue<BlockWrapper>();
    {
        this.blockWrapperQueue.add(new BlockWrapper(ID_TYPE, new UnitBlock(Endian.LITTLE, 2)));// header,type
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER_SIZE, new UnitBlock(Endian.LITTLE, 2)));// header,header
                                                                                                      // size
        this.blockWrapperQueue.add(new BlockWrapper(ID_BLOCK_SIZE, new UnitBlock(Endian.LITTLE, 4)));// header,file
                                                                                                     // size
    }

    protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        if (currentBlock.getTotalSize() == 0) {
            return;
        }
        switch (currentId) {
        case ID_TYPE:
            this.header.type = MathUtil.byteArrayToShort(currentBlock.getValue());
            Logger.log("type:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.header.type);
            break;
        case ID_HEADER_SIZE:
            this.header.size = MathUtil.byteArrayToShort(currentBlock.getValue());
            Logger.log("headerSize:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.header.size);
            break;
        case ID_BLOCK_SIZE:
            this.header.chunkSize = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("chunkSize:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + header.chunkSize);
            break;
        }
        super.afterRead(currentIndex, currentId, currentBlock);
    }

    protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
        return this.blockWrapperQueue;
    }
}
