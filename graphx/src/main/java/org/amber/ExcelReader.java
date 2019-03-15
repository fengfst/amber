package org.amber;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;

import java.util.List;
import java.util.Map;

public class ExcelReader {
    public static void main(String[] args) {
        cn.hutool.poi.excel.ExcelReader reader = ExcelUtil.getReader(FileUtil.file("relation.xlsx"));
        List<Map<String, Object>> maps = reader.readAll();

        System.out.println(1);
    }
}
