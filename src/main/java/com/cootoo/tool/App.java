package com.cootoo.tool;

import com.cootoo.tool.util.DBTool;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        App app = new App();
        app.testThreadSafe();
    }

    private void testDbTool() {
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

    private void testThreadSafe() {
        for (int i=0; i<500; i++) {
            new Thread(new DBThread((i+1)*100)).start();
        }
    }

}

class DBThread implements Runnable {
    private long sleep;
    public DBThread(long sleep) {
        this.sleep = sleep;
    }

    @Override
    public void run() {
        DBTool dbTool = DBTool.getInstant();
        List<Map<String, Object>> list = dbTool.list("select * from tLog limit 10");
        for (Map map : list) {
            System.out.println(String.valueOf(Thread.currentThread().getId()) + "\t-\t" + map.get("logID") + " : " + map.get("ip"));
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}