package org.amber;

import java.util.List;

/**
 * @Author: haifeng
 * @Date: 2019-03-15 15:15
 */
public class ExcelReadTest {
    public static void main(String[] args) {
        String file = "/Users/sundays/dev/git/amber/graphx/src/main/resources/relation.xlsx";

        List<SheetInfo> sheets = ExcelUtil.getContent(file, 0, 5);
        System.out.println(1);
    }
}
