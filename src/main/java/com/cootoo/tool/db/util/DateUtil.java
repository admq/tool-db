package com.cootoo.tool.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {

	private static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static String getTime() {
		return getTime(new Date());
	}

	public static String getTime(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat(FORMAT);
		return sf.format(date);
	}


}
