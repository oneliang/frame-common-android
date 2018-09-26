package com.oneliang.android.common.test.resource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.oneliang.Constants;
import com.oneliang.android.common.resources.parse.block.ArscBlock;
import com.oneliang.android.common.resources.parse.block.StringPoolBlock;
import com.oneliang.frame.io.CountingInputStream;

public class TestArscParse {

    private void testArscBlock() throws Exception {
        final String fileDirectory = "/D:/resourceArsc";
        final String filename = "resources#15219";
        System.setOut(new PrintStream(fileDirectory + Constants.Symbol.SLASH_LEFT + "log_" + filename + ".txt"));
        String file = fileDirectory + Constants.Symbol.SLASH_LEFT + filename + ".arsc";
        InputStream inputStream = new FileInputStream(file);
        CountingInputStream countingInputStream = new CountingInputStream(inputStream);
        ArscBlock arscBlock = new ArscBlock();
        arscBlock.parse(countingInputStream);
        arscBlock.print();
    }

    private void testArscStringDiff() throws Exception {
        final String fileDirectory = "/D:/resourceArsc";
        final String oldFilename = "resources#15196";
        final String newFilename = "resources#15198";
        // System.setOut(new PrintStream(fileDirectory +
        // Constants.Symbol.SLASH_LEFT + "log_" + filename + ".txt"));
        String oldFullFilename = fileDirectory + Constants.Symbol.SLASH_LEFT + oldFilename + ".arsc";
        String newFullFilename = fileDirectory + Constants.Symbol.SLASH_LEFT + newFilename + ".arsc";
        InputStream oldInputStream = new FileInputStream(oldFullFilename);
        CountingInputStream oldCountingInputStream = new CountingInputStream(oldInputStream);
        ArscBlock oldArscBlock = new ArscBlock();
        oldArscBlock.parse(oldCountingInputStream);

        Map<String, String> oldStringMap = new HashMap<String, String>();
        StringPoolBlock oldGlobalStringPoolBlock = oldArscBlock.getGlobalStringPoolBlock();
        for (int i = 0; i < oldGlobalStringPoolBlock.getStringPool().stringCount; i++) {
            String string = oldGlobalStringPoolBlock.getString(i);
            oldStringMap.put(string, string);
        }
        System.out.println("old:" + oldStringMap.size());
        InputStream newInputStream = new FileInputStream(newFullFilename);
        CountingInputStream newCountingInputStream = new CountingInputStream(newInputStream);
        ArscBlock newArscBlock = new ArscBlock();
        newArscBlock.parse(newCountingInputStream);
        StringPoolBlock newGlobalStringPoolBlock = newArscBlock.getGlobalStringPoolBlock();
        Map<String, String> newStringMap = new HashMap<String, String>();
        for (int i = 0; i < newGlobalStringPoolBlock.getStringPool().stringCount; i++) {
            String string = newGlobalStringPoolBlock.getString(i);
            newStringMap.put(string, string);
        }
        System.out.println("new:" + newStringMap.size());

        Map<String, String> bigMap = null;
        Map<String, String> smallMap = null;
        if (newStringMap.size() > oldStringMap.size()) {
            bigMap = newStringMap;
            smallMap = oldStringMap;
        } else {
            bigMap = oldStringMap;
            smallMap = newStringMap;
        }
        Iterator<Entry<String, String>> iterator = bigMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            if (!smallMap.containsKey(key)) {
                System.out.println(key + ", is not exists.");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TestArscParse test = new TestArscParse();
        long begin = System.currentTimeMillis();
        // test.testArscBlock();
        test.testArscStringDiff();
        System.out.println("block cost:" + (System.currentTimeMillis() - begin));
    }
}
