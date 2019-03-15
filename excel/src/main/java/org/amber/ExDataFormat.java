package org.amber;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Date;

/**
 * @Author: haifeng
 * @Date: 2019-03-15 16:04
 */
public class ExDataFormat extends DataFormatter {

    @Override
    public String formatRawCellContents(double value, int formatIndex, String formatString) {
        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            Date date = DateUtil.getJavaDate(value);
            String s = "s", h = "h";
            if (formatString.toLowerCase().contains(s) || formatString.toLowerCase().contains(h)) {
                return DateFormatUtils.format(date, "yyyy-MM-dd hh:mm:ss");
            } else {
                return DateFormatUtils.format(date, "yyyy-MM-dd");
            }
        } else {
            return super.formatRawCellContents(value, formatIndex, formatString);
        }
    }

}
