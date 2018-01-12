package com.oneliang.android.common.resources.parse.block;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.android.common.resources.parse.struct.StringPool;
import com.oneliang.android.common.resources.xml.ResXmlEncoders;
import com.oneliang.frame.section.Block;
import com.oneliang.frame.section.BlockWrapper;
import com.oneliang.frame.section.ComplexBlock;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.UnitBlock;
import com.oneliang.frame.section.UnitSection;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;

public class StringPoolBlock extends ComplexBlock {

    // id
    private static final int ID_HEADER = 1;
    private static final int ID_STRING_COUNT = 2;
    private static final int ID_STYLE_COUNT = 3;
    private static final int ID_FLAGS = 4;
    private static final int ID_STRING_START = 5;
    private static final int ID_STYLE_START = 6;
    private static final int ID_STRING_OFFSET_ARRAY = 7;
    private static final int ID_STYLE_OFFSET_ARRAY = 8;
    private static final int ID_STRING_CONTENT = 9;
    private static final int ID_STYLE_CONTENT = 10;
    // struct
    final StringPool stringPool = new StringPool();
    // block
    private HeaderBlock headerBlock = new HeaderBlock();
    private Block stringCountBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block styleCountBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block flagsBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block stringStartBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block styleStartBlock = new UnitBlock(Endian.LITTLE, 4);
    private Block stringOffsetArrayBlock = new UnitBlock(0);
    private Block styleOffsetArrayBlock = new UnitBlock(0);
    private Block stringContentBlock = new UnitBlock(0);
    private Block styleContentBlock = new UnitBlock(0);

    private Queue<BlockWrapper> blockWrapperQueue = new ConcurrentLinkedQueue<BlockWrapper>();
    private final CharsetDecoder UTF16LE_DECODER = Charset.forName("UTF-16LE").newDecoder();
    private final CharsetDecoder UTF8_DECODER = Charset.forName("UTF-8").newDecoder();

    {
        this.blockWrapperQueue.add(new BlockWrapper(ID_HEADER, this.headerBlock));// header
        this.blockWrapperQueue.add(new BlockWrapper(ID_STRING_COUNT, this.stringCountBlock));// resource
                                                                                             // type
                                                                                             // chunk,string
                                                                                             // count
        this.blockWrapperQueue.add(new BlockWrapper(ID_STYLE_COUNT, this.styleCountBlock));// resource
                                                                                           // type
                                                                                           // chunk,style
                                                                                           // count
        this.blockWrapperQueue.add(new BlockWrapper(ID_FLAGS, this.flagsBlock));// resource
                                                                                // type
                                                                                // chunk,flags
        this.blockWrapperQueue.add(new BlockWrapper(ID_STRING_START, this.stringStartBlock));// resource
                                                                                             // type
                                                                                             // chunk,string
                                                                                             // start
        this.blockWrapperQueue.add(new BlockWrapper(ID_STYLE_START, this.styleStartBlock));// resource
                                                                                           // type
                                                                                           // chunk,styles
                                                                                           // start,header
                                                                                           // count
                                                                                           // 28byte
        this.blockWrapperQueue.add(new BlockWrapper(ID_STRING_OFFSET_ARRAY, this.stringOffsetArrayBlock));// resource
                                                                                                          // string
                                                                                                          // pool,string
                                                                                                          // offset
                                                                                                          // array
        this.blockWrapperQueue.add(new BlockWrapper(ID_STYLE_OFFSET_ARRAY, this.styleOffsetArrayBlock));// resource
                                                                                                        // string
                                                                                                        // pool,style
                                                                                                        // offset
                                                                                                        // array
        this.blockWrapperQueue.add(new BlockWrapper(ID_STRING_CONTENT, this.stringContentBlock));// resource
                                                                                                 // string
                                                                                                 // pool,string
                                                                                                 // content
        this.blockWrapperQueue.add(new BlockWrapper(ID_STYLE_CONTENT, this.styleContentBlock));// resource
                                                                                               // string
                                                                                               // pool,style
                                                                                               // content
    }

    protected void beforeRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            Logger.log("----------resource string pool----------");
            break;
        case ID_STRING_OFFSET_ARRAY:
            int size = this.stringPool.stringCount * 4;
            currentBlock.setInitialSize(size);
            break;
        case ID_STYLE_OFFSET_ARRAY:
            size = this.stringPool.styleCount * 4;
            currentBlock.setInitialSize(size);
            break;
        case ID_STRING_CONTENT:
            size = ((this.stringPool.stylesStart == 0) ? this.headerBlock.header.chunkSize : this.stringPool.stylesStart) - this.stringPool.stringsStart;
            currentBlock.setInitialSize(size);
            break;
        case ID_STYLE_CONTENT:
            size = this.stringPool.stylesStart == 0 ? 0 : (this.headerBlock.header.chunkSize - this.stringPool.stylesStart);
            currentBlock.setInitialSize(size);
            break;
        }
    }

    protected void afterRead(int currentIndex, int currentId, Block currentBlock) throws Exception {
        switch (currentId) {
        case ID_HEADER:
            this.stringPool.header = headerBlock.header;
            break;
        case ID_STRING_COUNT:
            this.stringPool.stringCount = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("stringCount:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.stringPool.stringCount);
            break;
        case ID_STYLE_COUNT:
            this.stringPool.styleCount = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("styleCount:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.stringPool.styleCount);
            break;
        case ID_FLAGS:
            this.stringPool.flags = MathUtil.byteArrayToInt(currentBlock.getValue());
            this.stringPool.isUTF8 = (this.stringPool.flags & StringPool.UTF8_FLAG) != 0;
            Logger.log("flags:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.stringPool.flags);
            break;
        case ID_STRING_START:
            this.stringPool.stringsStart = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("stringsStart:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.stringPool.stringsStart);
            break;
        case ID_STYLE_START:
            this.stringPool.stylesStart = MathUtil.byteArrayToInt(currentBlock.getValue());
            Logger.log("stylesStart:0x" + StringUtil.byteArrayToHexString(currentBlock.getValue()) + "," + this.stringPool.stylesStart);
            break;
        case ID_STRING_OFFSET_ARRAY: {
            byte[] byteArray = currentBlock.getValue();
            ByteArrayInputStream offsetByteArrayInputStream = new ByteArrayInputStream(byteArray);
            this.stringPool.stringOffsetArray = new int[this.stringPool.stringCount];
            for (int i = 0; i < this.stringPool.stringCount; i++) {
                this.stringPool.stringOffsetArray[i] = nextOffset(offsetByteArrayInputStream);
            }
            Logger.log("string start:" + stringPool.stringsStart + ",string array length:" + this.stringPool.stringCount * 4);

            this.stringPool.stringOwns = new int[this.stringPool.stringCount];
            Arrays.fill(this.stringPool.stringOwns, -1);
        }
            break;
        case ID_STYLE_OFFSET_ARRAY: {
            byte[] byteArray = currentBlock.getValue();
            ByteArrayInputStream offsetByteArrayInputStream = new ByteArrayInputStream(byteArray);
            this.stringPool.styleOffsetArray = new int[this.stringPool.styleCount];
            for (int i = 0; i < this.stringPool.styleCount; i++) {
                this.stringPool.styleOffsetArray[i] = nextOffset(offsetByteArrayInputStream);
            }
            Logger.log("style start:" + stringPool.stylesStart + ",style array length:" + this.stringPool.styleCount * 4);
        }
            break;
        case ID_STRING_CONTENT: {
            byte[] byteArray = currentBlock.getValue();
            this.stringPool.allStringByteArray = byteArray;
        }
            break;
        case ID_STYLE_CONTENT: {
            byte[] byteArray = currentBlock.getValue();
            if (this.stringPool.stylesStart != 0) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                int styleArrayLength = byteArray.length / 4;
                this.stringPool.styleArray = new int[styleArrayLength];
                for (int i = 0; i < styleArrayLength; i++) {
                    byte[] buffer = new byte[4];
                    int length = byteArrayInputStream.read(buffer, 0, buffer.length);
                    int value = 0;
                    if (length == buffer.length) {
                        value = MathUtil.byteArrayToInt(MathUtil.byteArrayReverse(buffer));
                    }
                    this.stringPool.styleArray[i] = value;
                }
                // // read remaining bytes
                // int remaining = byteArray % 4;
                // if (remaining >= 1) {
                // while (remaining-- > 0) {
                // reader.readByte();
                // }
                // }
            }
        }
            break;
        }
        super.afterRead(currentIndex, currentId, currentBlock);
    }

    private int nextOffset(ByteArrayInputStream offsetByteArrayInputStream) {
        int offset = 0;
        byte[] buffer = new byte[4];
        int length = offsetByteArrayInputStream.read(buffer, 0, buffer.length);
        if (length == buffer.length) {
            offset = MathUtil.byteArrayToInt(MathUtil.byteArrayReverse(buffer));
        }
        return offset;
    }

    protected Queue<BlockWrapper> getParseBlockWrapperQueue() {
        return this.blockWrapperQueue;
    }

    public String getString(int index) {
        if (index < 0 || this.stringPool.stringOffsetArray == null || index >= this.stringPool.stringOffsetArray.length) {
            return null;
        }
        int offset = this.stringPool.stringOffsetArray[index];
        int length;

        if (this.stringPool.isUTF8) {
            int[] val = getUtf8(this.stringPool.allStringByteArray, offset);
            offset = val[0];
            length = val[1];
        } else {
            int[] val = getUtf16(this.stringPool.allStringByteArray, offset);
            offset += val[0];
            length = val[1];
        }
        return decodeString(offset, length);
    }

    /**
     * Returns string with style tags (html-like).
     */
    public String getHTML(int index) {
        String raw = getString(index);
        if (raw == null) {
            return raw;
        }
        int[] style = getStyle(index);
        if (style == null) {
            return ResXmlEncoders.escapeXmlChars(raw);
        }
        StringBuilder html = new StringBuilder(raw.length() + 32);
        int[] opened = new int[style.length / 3];
        int offset = 0, depth = 0;
        while (true) {
            int i = -1, j;
            for (j = 0; j != style.length; j += 3) {
                if (style[j + 1] == -1) {
                    continue;
                }
                if (i == -1 || style[i + 1] > style[j + 1]) {
                    i = j;
                }
            }
            int start = ((i != -1) ? style[i + 1] : raw.length());
            for (j = depth - 1; j >= 0; j--) {
                int last = opened[j];
                int end = style[last + 2];
                if (end >= start) {
                    break;
                }
                if (offset <= end) {
                    html.append(ResXmlEncoders.escapeXmlChars(raw.substring(offset, end + 1)));
                    offset = end + 1;
                }
                outputStyleTag(getString(style[last]), html, true);
            }
            depth = j + 1;
            if (offset < start) {
                html.append(ResXmlEncoders.escapeXmlChars(raw.substring(offset, start)));
                offset = start;
            }
            if (i == -1) {
                break;
            }
            outputStyleTag(getString(style[i]), html, false);
            style[i + 1] = -1;
            opened[depth++] = i;
        }
        return html.toString();
    }

    /**
     * Returns style information - array of int triplets, where in each triplet:
     * * first int is index of tag name ('b','i', etc.) * second int is tag
     * start index in string * third int is tag end index in string
     */
    public int[] getStyle(int index) {
        if (this.stringPool.styleOffsetArray == null || this.stringPool.styleArray == null || index >= this.stringPool.styleOffsetArray.length) {
            return null;
        }
        int offset = this.stringPool.styleOffsetArray[index] / 4;
        int style[];
        {
            int count = 0;
            for (int i = offset; i < this.stringPool.styleArray.length; ++i) {
                if (this.stringPool.styleArray[i] == -1) {
                    break;
                }
                count += 1;
            }
            if (count == 0 || (count % 3) != 0) {
                return null;
            }
            style = new int[count];
        }
        for (int i = offset, j = 0; i < this.stringPool.styleArray.length;) {
            if (this.stringPool.styleArray[i] == -1) {
                break;
            }
            style[j++] = this.stringPool.styleArray[i++];
        }
        return style;
    }

    private void outputStyleTag(String tag, StringBuilder builder, boolean close) {
        builder.append('<');
        if (close) {
            builder.append('/');
        }

        int pos = tag.indexOf(';');
        if (pos == -1) {
            builder.append(tag);
        } else {
            builder.append(tag.substring(0, pos));
            if (!close) {
                boolean loop = true;
                while (loop) {
                    int pos2 = tag.indexOf('=', pos + 1);

                    // malformed style information will cause crash. so
                    // prematurely end style tags, if recreation
                    // cannot be created.
                    if (pos2 != -1) {
                        builder.append(' ').append(tag.substring(pos + 1, pos2)).append("=\"");
                        pos = tag.indexOf(';', pos2 + 1);

                        String val;
                        if (pos != -1) {
                            val = tag.substring(pos2 + 1, pos);
                        } else {
                            loop = false;
                            val = tag.substring(pos2 + 1);
                        }

                        builder.append(ResXmlEncoders.escapeXmlChars(val)).append('"');
                    } else {
                        loop = false;
                    }

                }
            }
        }
        builder.append('>');
    }

    private String decodeString(int offset, int length) {
        try {
            return (this.stringPool.isUTF8 ? UTF8_DECODER : UTF16LE_DECODER).decode(ByteBuffer.wrap(this.stringPool.allStringByteArray, offset, length)).toString();
        } catch (CharacterCodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static final int[] getUtf8(byte[] array, int offset) {
        int val = array[offset];
        int length;

        if ((val & 0x80) != 0) {
            offset += 2;
        } else {
            offset += 1;
        }
        val = array[offset];
        if ((val & 0x80) != 0) {
            offset += 2;
        } else {
            offset += 1;
        }
        length = 0;
        while (array[offset + length] != 0) {
            length++;
        }
        return new int[] { offset, length };
    }

    private static final int[] getUtf16(byte[] array, int offset) {
        int val = ((array[offset + 1] & 0xFF) << 8 | array[offset] & 0xFF);

        if (val == 0x8000) {
            int high = (array[offset + 3] & 0xFF) << 8;
            int low = (array[offset + 2] & 0xFF);
            return new int[] { 4, (high + low) * 2 };
        }
        return new int[] { 2, val * 2 };
    }

    /**
     * @return the stringPool
     */
    public StringPool getStringPool() {
        return stringPool;
    }

    /**
     * to section list
     * 
     * @return List<Block>
     */
    public List<Section> toSectionList() {
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.add(this.headerBlock);
        sectionList.add(this.stringCountBlock);
        sectionList.add(this.styleCountBlock);
        sectionList.add(this.flagsBlock);
        sectionList.add(this.stringStartBlock);
        sectionList.add(this.styleStartBlock);
        sectionList.add(this.stringOffsetArrayBlock);
        sectionList.add(this.styleOffsetArrayBlock);
        for (int i = 0; i < this.stringPool.stringCount; i++) {
            int beginOffset = this.stringPool.stringOffsetArray[i];
            int endOffset = 0;
            if (i + 1 < this.stringPool.stringCount) {// last
                endOffset = this.stringPool.stringOffsetArray[i + 1];
            } else {
                endOffset = this.stringPool.allStringByteArray.length;
            }
            // Logger.log(beginOffset+","+endOffset);
            int length = endOffset - beginOffset;
            byte[] byteArray = new byte[length];
            System.arraycopy(this.stringPool.allStringByteArray, beginOffset, byteArray, 0, length);
            sectionList.add(new UnitSection(byteArray));
        }
        sectionList.add(this.styleContentBlock);
        return sectionList;
    }
}
