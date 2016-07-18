package com.cootoo.tool;

import com.cootoo.tool.util.DBTool;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        DBTool dbTool = DBTool.getInstant();
        List<Map<String, Object>> list = dbTool.list("select * from tLog limit 10");
        for (Map map : list) {
            System.out.println(map.get("logID") + " : " + map.get("ip"));
        }

        System.out.println();
        System.out.println();
        System.out.println();

        List<Log> logs = dbTool.list("select * from tLog limit 1000", Log.class);
        int s = 1;
        for (Log log : logs) {
            System.out.println("[" + s++ + "] : " + log.toString());
        }

        System.out.println();
        System.out.println();
        System.out.println();

        Log one = dbTool.getOne("select * from tLog limit 1", Log.class);
        System.out.println(one);

    }
}
