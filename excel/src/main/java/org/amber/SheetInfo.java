package org.amber;

import java.util.List;
import java.util.Map;

/**
 * @Author: haifeng
 * @Date: 2019-03-15 15:12
 */
public class SheetInfo {
    private String sheetName;
    private int index;
    private List<String> header;
    private List<Map<String, String>> data;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public SheetInfo() {
    }
}
