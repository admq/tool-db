package com.cootoo.tool.db.util;

import java.io.File;

/**
 * Created by larry on 16/8/25.
 */
public class AppUtil {
    //程序运行目录
    public static String rootPath = "";
    public static String webClassPath = "";

    static {
        String path = AppUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.endsWith(File.separator)) {
            AppUtil.rootPath = path;
        } else {
            AppUtil.rootPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
        }
    }
}
