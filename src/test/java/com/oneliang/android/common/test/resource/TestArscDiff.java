package com.oneliang.android.common.test.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.Constant;
import com.oneliang.android.common.resources.parse.block.ArscBlock;
import com.oneliang.android.common.resources.parse.block.StringPoolBlock;
import com.oneliang.android.common.resources.parse.block.TablePackageBlock;
import com.oneliang.android.common.resources.parse.block.TableTypeSpecBlock;
import com.oneliang.frame.io.CountingInputStream;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.SectionDiff;
import com.oneliang.frame.section.SectionDiffData;
import com.oneliang.frame.section.SectionPosition;
import com.oneliang.util.common.Generator;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public class TestArscDiff {

    public static List<Section> arscToSectionList(String arscFullFilename) throws Exception {
        List<Section> sectionList = new ArrayList<Section>();
        InputStream arscInputStream = new FileInputStream(arscFullFilename);
        CountingInputStream countingInputStream = new CountingInputStream(arscInputStream);
        ArscBlock arscBlock = new ArscBlock();
        // arscBlock.parse(arscInputStream);
        arscBlock.parse(countingInputStream);
        // arsc section
        sectionList.add(arscBlock.getHeaderBlock());
        sectionList.add(arscBlock.getPackageCountBlock());
        // global string pool section
        StringPoolBlock globalStringPoolBlock = arscBlock.getGlobalStringPoolBlock();
        sectionList.addAll(globalStringPoolBlock.toSectionList());
        // table package section
        List<TablePackageBlock> tablePackageBlockList = arscBlock.getTablePackageBlockList();
        for (TablePackageBlock tablePackageBlock : tablePackageBlockList) {
            sectionList.addAll(tablePackageBlock.toSectionList());
            // resource type string pool
            StringPoolBlock resourceTypeStringPoolBlock = tablePackageBlock.getResourceTypeStringPoolBlock();
            sectionList.addAll(resourceTypeStringPoolBlock.toSectionList());
            // resource keyword string pool
            StringPoolBlock resourceKeywordStringPoolBlock = tablePackageBlock.getResourceKeywordStringPoolBlock();
            sectionList.addAll(resourceKeywordStringPoolBlock.toSectionList());
            List<TableTypeSpecBlock> tableTypeSpecBlockList = tablePackageBlock.getTableTypeSpecBlockList();
            for (TableTypeSpecBlock tableTypeSpecBlock : tableTypeSpecBlockList) {
                sectionList.addAll(tableTypeSpecBlock.toSectionList());
            }
        }
        return sectionList;
    }

    public static void main(String[] args) throws Exception {
        final String fileDirectory = "/D:/resourceArsc";
        final String diffFile = fileDirectory + Constant.Symbol.SLASH_LEFT + "resources.diff";
        String oldArscFile = fileDirectory + Constant.Symbol.SLASH_LEFT + "resources#15196.arsc";
        String newArscFile = fileDirectory + Constant.Symbol.SLASH_LEFT + "resources#15197.arsc";
        System.setOut(new PrintStream(fileDirectory + Constant.Symbol.SLASH_LEFT + "log_resource_diff.txt"));
        SectionDiff sectionDiff = new SectionDiff();
        long begin = System.currentTimeMillis();
        List<Section> oldSectionList = arscToSectionList(oldArscFile);
        List<Section> newSectionList = arscToSectionList(newArscFile);
        System.out.println("after parse:" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        List<SectionPosition> sectionPositionMoveList = new ArrayList<SectionPosition>();
        // sectionPositionMoveList.add(new SectionPosition(1,1));
        // sectionPositionMoveList.add(new SectionPosition(2,2));
        // sectionPositionMoveList.add(new SectionPosition(3,3));
        // sectionDiff.mergeSectionPositionMoveList(sectionPositionMoveList);
        SectionDiffData sectionDiffData = sectionDiff.diff(oldSectionList, newSectionList);
        System.out.println("after diff:" + (System.currentTimeMillis() - begin));
        FileUtil.writeFile(diffFile, sectionDiffData.toByteArray());
        System.out.println("diff data length:" + sectionDiffData.toByteArray().length);
        begin = System.currentTimeMillis();
        sectionDiff.patch(oldSectionList, sectionDiffData.sectionPositionMoveList, sectionDiffData.sectionPositionIncreaseList);
        System.out.println("after patch:" + (System.currentTimeMillis() - begin));
        // sectionDiff.printSectionList(oldSectionList);
        System.out.println("length:" + FileUtil.readFile(oldArscFile).length + "oldMD5:" + Generator.MD5File(oldArscFile));
        // sectionDiff.printSectionList(newSectionList);
        System.out.println("length:" + FileUtil.readFile(newArscFile).length + "newMD5:" + Generator.MD5File(newArscFile));
    }
}
