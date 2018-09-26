package com.oneliang.android.common.test.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.Constants;
import com.oneliang.android.common.resources.parse.block.ArscBlock;
import com.oneliang.android.common.resources.parse.block.StringPoolBlock;
import com.oneliang.android.common.resources.parse.block.TablePackageBlock;
import com.oneliang.android.common.resources.parse.block.TableTypeSpecBlock;
import com.oneliang.frame.io.CountingInputStream;
import com.oneliang.frame.section.Section;
import com.oneliang.frame.section.SectionDiff;
import com.oneliang.frame.section.SectionDiffData;
import com.oneliang.frame.section.SectionPosition;
import com.oneliang.util.bsdiff.BSDiff;
import com.oneliang.util.common.Generator;
import com.oneliang.util.common.MathUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public class TestArscDiff {

    private static void testBsDiff() throws Exception {
        final String fileDirectory = "/D:/resourceArsc";
        File oldFile = new File(fileDirectory + "/resources#15227.arsc");
        File newFile = new File(fileDirectory + "/resources#15229.arsc");
        File diffFile = new File(fileDirectory + "/resources_bsdiff.arsc");
        BSDiff.bsdiff(oldFile, newFile, diffFile);
    }

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
        // testBsDiff();
        // List<SectionPosition> sectionPositionMoveList = new
        // ArrayList<SectionPosition>();
        // sectionPositionMoveList.add(new SectionPosition(1, 1));
        // sectionPositionMoveList.add(new SectionPosition(-1, 2));
        // sectionPositionMoveList.add(new SectionPosition(5, 3));
        // sectionPositionMoveList.add(new SectionPosition(6, 4));
        // SectionDiffData.mergeSectionPosition(sectionPositionMoveList);
        // System.exit(0);
        final String fileDirectory = "/D:/resourceArsc";
        final String diffFile = fileDirectory + Constants.Symbol.SLASH_LEFT + "resources_diff.arsc";
        String oldArscFile = fileDirectory + Constants.Symbol.SLASH_LEFT + "resources#15227.arsc";
        String newArscFile = fileDirectory + Constants.Symbol.SLASH_LEFT + "resources#15229.arsc";
        String afterPatchArscFile = fileDirectory + Constants.Symbol.SLASH_LEFT + "resources_patch.arsc";
        System.setOut(new PrintStream(fileDirectory + Constants.Symbol.SLASH_LEFT + "log_resource_diff.txt"));
        SectionDiff sectionDiff = new SectionDiff();
        long begin = System.currentTimeMillis();
        List<Section> oldSectionList = arscToSectionList(oldArscFile);
        List<Section> newSectionList = arscToSectionList(newArscFile);
        System.out.println("after parse:" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        SectionDiffData sectionDiffData = sectionDiff.diff(oldSectionList, newSectionList);
        System.out.println("after diff:" + (System.currentTimeMillis() - begin));
        FileUtil.writeFile(diffFile, sectionDiffData.toByteArray());

        System.out.println("diff data length:" + sectionDiffData.toByteArray().length);
        begin = System.currentTimeMillis();
        System.out.println("move size:" + sectionDiffData.sectionPositionMoveList.size());
        System.out.println("increase size:" + sectionDiffData.sectionPositionIncreaseList.size());
        System.out.println("move byte size:" + sectionDiffData.sectionPositionMoveList.size() * 4);
        System.out.println("----------start move----------");
        int increaseByteSize = 0;
        for (SectionPosition sectionPosition : sectionDiffData.sectionPositionMoveList) {
            System.out.println(String.format("fetch from (new<-old)(index:%s <- %s)", sectionPosition.getToIndex(), sectionPosition.getFromIndex()));
        }
        for (SectionPosition sectionPosition : sectionDiffData.sectionPositionIncreaseList) {
            byte[] byteArray = sectionPosition.getByteArray();
            // if (byteArray == null) {
            // System.out.println("byte array is null:" +
            // sectionPosition.getToIndex());
            // } else {
            increaseByteSize += sectionPosition.getByteArray().length;
            // }
        }
        System.out.println("increase byte size:" + increaseByteSize);
        System.out.println("----------end move----------");

        SectionDiffData.mergeSectionPosition(sectionDiffData.sectionPositionMoveList);

        sectionDiffData = SectionDiffData.parseFrom(new FileInputStream(diffFile));
        byte[] byteArray = sectionDiff.patch(oldSectionList, sectionDiffData.sectionPositionMoveList, sectionDiffData.sectionPositionIncreaseList);
        FileUtil.writeFile(afterPatchArscFile, byteArray);
        System.out.println("after patch:" + (System.currentTimeMillis() - begin));
        System.out.println("length:" + FileUtil.readFile(oldArscFile).length + ",oldMD5:" + Generator.MD5File(oldArscFile));
        System.out.println("length:" + FileUtil.readFile(newArscFile).length + ",newMD5:" + Generator.MD5File(newArscFile));
        System.out.println("length:" + FileUtil.readFile(afterPatchArscFile).length + ",afterPatchMD5:" + Generator.MD5File(afterPatchArscFile));
    }
}
