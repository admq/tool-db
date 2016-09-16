package com.cootoo.tool.db;

/**
 * Created by larry on 16/7/12.
 */
public class DBConfig {
    public String url = "jdbc:mysql://192.168.0.9:3306/?characterEncoding=utf8&useSSL=false";
    public String username = "root";
    public String password = "root";
    public String dbname = null;
    public Integer port = 3306;
    public Integer connectTimeOut = 3;

    public boolean autoCommit = true;


    public String table_prex = "";

    //数据库类型
    public String driver = null;

}
