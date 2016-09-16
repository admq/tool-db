package com.cootoo.tool.db.util;

import java.util.Date;

/**
 * Created by larry on 16/7/12.
 */
public class SQLUtil {
    public static final String NOW = "now()";

    public static String appendColumn(Object val) {
        if (val == null) return null;
        if (NOW.equals(val)) {
            return NOW;
        } else if (val instanceof String || val instanceof Character) {
            return "'"+val.toString().trim()+"'";
        } else if (val instanceof Date) {
            return "'"+DateUtil.getTime((Date) val)+"'";
        }
        else return val.toString();
    }

    public static String appendRow(Object ... vals) {
        String row = "(";
        for (Object o : vals) {
            row += appendColumn(o) + ",";
        }
        row = row.substring(0, row.length()-1) + ")";
        return row;
    }

}
