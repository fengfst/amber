package org.amber;

import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author Sundays
 * @date 17-11-9 15:02
 */
public class ExcelUtil {

    private final static Logger logger = Logger.getLogger(ExcelUtil.class);
    private List<SheetInfo> sheets = new ArrayList<>();
    private String filename;
    private int readRow;
    private int headRow;
    private InputStream currentStream;
    private boolean ignoreEmptyRow = false;
    private List<Map<String, String>> currentSheetContent;
    private List<String> header = new ArrayList<>();
    private SheetInfo currentSheetInfo;
    private List<String> lastRow;

    private int maxColumn = 0;

    public List<SheetInfo> getSheet() {
        return sheets;
    }

    private Map<String, String> toMap(List<String> header, List<String> rowData) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String head = header.get(i).intern();
            String value = "";
            if (rowData != null || i < rowData.size()) {
                value = rowData.get(i);
            }
            map.put(head, value);
        }
        return map;
    }

    private class SheetHandler implements SheetContentsHandler {
        private boolean firstCellOfRow;
        private int currentRow = -1;
        private int currentCol = -1;
        private List<String> rowData;

        private void outputMissingRows(int number) {
            if (!ignoreEmptyRow) {
                for (int i = 0; i < number; i++) {
                    for (int j = 0; j < maxColumn; j++) {
                        rowData.add("");
                    }
                    currentSheetContent.add(toMap(header, rowData));
                    rowData = new ArrayList<>();
                }
            }
        }

        @Override
        public void startRow(int rowNum) {
            rowData = new ArrayList<>();
            // If there were gaps, output the missing rows
            outputMissingRows(rowNum - currentRow - 1);
            // Prepare for this row
            firstCellOfRow = true;
            currentRow = rowNum;
            currentCol = -1;
        }

        @Override
        public void endRow(int rowNum) {
            lastRow = rowData;
            if (rowNum > headRow - 1 && rowNum < headRow + readRow) {
                if (rowNum == headRow) {
                    header = rowData;
                    currentSheetInfo.setHeader(header);
                } else {
                    currentSheetContent.add(toMap(header, rowData));
                }
            } else if (rowNum >= headRow + readRow) {
                try {
                    currentStream.close();
                } catch (IOException e) {
                    System.out.println("end read this sheet");
                }
            }

        }

        @Override
        public void cell(String cellReference, String formattedValue,
                         XSSFComment comment) {
            if (firstCellOfRow) {
                firstCellOfRow = false;
            }

            // gracefully handle missing CellRef here in a similar way as XSSFCell does
            if (cellReference == null) {
                cellReference = new CellAddress(currentRow, currentCol).formatAsString();
            }

            // Did we miss any cells?
            int thisCol = (new CellReference(cellReference)).getCol();
            int missedCols = thisCol - currentCol - 1;
            for (int i = 0; i < missedCols; i++) {
                rowData.add("");
            }
            currentCol = thisCol;
            rowData.add(formattedValue);

        }

        @Override
        public void headerFooter(String s, boolean b, String s1) {

        }
    }


    /**
     * .
     *
     * @throws OpenXML4JException .
     * @throws IOException        .
     * @throws SAXException       .
     */
    private void process() throws Exception {
        OPCPackage p = OPCPackage.open(filename, PackageAccess.READ);
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(p);
        XSSFReader xssfReader = new XSSFReader(p);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
            currentStream = iter.next();
            String sheetName = iter.getSheetName();
            final SheetInfo sheet = new SheetInfo();
            currentSheetInfo = sheet;
            sheet.setSheetName(sheetName);
            sheet.setIndex(index);
            currentSheetContent = new ArrayList<>();
            sheet.setData(currentSheetContent);
            sheet.setHeader(header);
            sheets.add(sheet);
            processSheet(styles, strings, new SheetHandler(), currentStream);
            try {
                currentStream.close();
            } catch (Exception e) {
                logger.info("end read this sheet:" + sheetName);
            }
            ++index;
        }
    }


    /**
     * .
     *
     * @param styles           .
     * @param strings          .
     * @param sheetHandler     .
     * @param sheetInputStream .
     * @throws IOException  .
     * @throws SAXException .
     */
    private void processSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            SheetContentsHandler sheetHandler,
            InputStream sheetInputStream) throws IOException, SAXException {
        // DataFormatter formatter = new DataFormatter();
        //PoiDataFormatter formatter = new PoiDataFormatter();
        ExDataFormat formatter = new ExDataFormat();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            try {
                sheetParser.parse(sheetSource);
            } catch (Exception e) {
                logger.warn("force end this sheet read");
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }


    private ExcelUtil(String filename, int headRow, int readRow) {
        this.filename = filename;
        this.headRow = headRow;
        this.readRow = readRow + 1;
        try {
            process();
        } catch (Exception e) {
            throw new RuntimeException("process excel error");
        }
    }

    public static List<SheetInfo> getContent(String file, int headRow, int readRow) {
        ExcelUtil excelUtil = new ExcelUtil(file, headRow, readRow);
        return excelUtil.getSheet();
    }

    private void rowEnd(JSONArray row, int rowNum) {

    }
}