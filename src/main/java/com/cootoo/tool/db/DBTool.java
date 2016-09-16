package com.cootoo.tool.db;

import com.cootoo.tool.db.util.AppUtil;
import com.sun.rowset.JdbcRowSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.JdbcRowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by larry on 16/6/29.
 */
@SuppressWarnings("unchecked")
public class DBTool {
    private static Logger logger = LoggerFactory.getLogger(DBTool.class);
    //private static DBTool dbTool = null;
    //private static Map<String, DBTool> dbTools = new HashMap<>();
    private static String configFile = "dbTool.properties";
    private Properties prop;
    private String configPath = null;

    private DBConfig dbConfig = new DBConfig();
    private Connection conn = null;

    static {
        staticConfig();
    }

    private static void staticConfig() {
        String path = DBTool.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        URL url = DBTool.class.getClassLoader().getResource("");
        AppUtil.webClassPath = (url == null ? "" : url.getPath());
        if (path.endsWith(File.separator)) {
            AppUtil.rootPath = path;
            //configFile = path + configFile;
        } else {
            AppUtil.rootPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
            //configFile = path.substring(0, path.lastIndexOf(File.separator) + 1) + configFile;
        }

    }

    private DBTool(String configFile) throws Exception {
        initConfig(configFile);
        init();
    }

    private void initConfig(String configFile) throws Exception {
        configPath = AppUtil.rootPath + configFile;
        if (!new File(configPath).isFile()) {
            configPath = AppUtil.webClassPath + configFile;
            logger.debug("WebClassPath : " + configPath);
        } else {
            logger.debug("AppRootPath : " + configPath);
        }
        if (!new File(configPath).isFile()) {
            logger.error("Not find the dbTool.properties in class path [{}]", configPath);
            throw new Exception("Not find the dbTool.properties in class path !");
        }
        logger.debug("configFile : " + configFile);
        loadProp(configPath);
    }

    private void init() {
        //if (dbTool != null) return;
        try {

            if (conn != null) {
                logger.debug("dbTool has initiated ");
            }
            logger.debug("init dbTool ... ");
            if (dbConfig.driver != null) {
                Class.forName(dbConfig.driver);
            } else {
                Class.forName("com.mysql.jdbc.Driver");
            }
            logger.debug("Connecting : " + dbConfig.url);

            DriverManager.setLoginTimeout(dbConfig.connectTimeOut);
            conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
            conn.setAutoCommit(dbConfig.autoCommit);
            // use database
            if (dbConfig.dbname != null) {
                JdbcRowSet jrs = new JdbcRowSetImpl(conn);
                jrs.setCommand("use " + dbConfig.dbname);
                jrs.execute();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("获取数据库连接失败,1分钟后尝试重连 ...");
            try {
                Thread.sleep(60000);
                init();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void loadProp(String configPath) {
        if (prop != null) return;
        prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(configPath)));
            //config connect properties
            dbConfig.url = prop.getProperty("url");
            dbConfig.username = prop.getProperty("username");
            dbConfig.password = prop.getProperty("password");
            dbConfig.dbname = prop.getProperty("dbname");
            dbConfig.driver = prop.getProperty("driver");
            if (prop.getProperty("autoCommit") != null)
                dbConfig.autoCommit = Boolean.getBoolean(prop.getProperty("autoCommit"));
            if (prop.getProperty("connectTimeOut") != null)
                dbConfig.connectTimeOut = Integer.parseInt(prop.getProperty("connectTimeOut"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询出一条记录,结果以 Map<String, Object> 形式返回
     *
     * @param sql
     * @return
     */
    public Map<String, Object> getOne(String sql) {
        List<Map<String, Object>> result = getResultMapList(sql);
        if (result.size() > 0) return result.get(0);
        else return null;
    }

    /**
     * 查询出一条记录,结果以 T(泛型) 形式返回
     *
     * @param sql
     * @return
     */
    public <T> T getOne(String sql, Class<T> clasz) {
        List<T> result = getResultObjtList(sql, clasz);
        if (result.size() > 0) return result.get(0);
        else return null;
    }

    /**
     * 查询多条记录,结果以 List<Map<String, Object>> 形式返回
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> list(String sql) {
        return getResultMapList(sql);
    }

    /**
     * 查询多条记录,结果以 List<T>(泛型) 形式返回
     *
     * @param sql
     * @return
     */
    public <T> List<T> list(String sql, Class<T> clasz) {
        return getResultObjtList(sql, clasz);
    }

    /**
     * 插入记录
     *
     * @param sql
     * @return
     */
    public int insertInto(String sql) {
        return executeUpdate(sql);
    }

    /**
     * 更新记录
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        return executeUpdate(sql);
    }


    /**
     * 执行SQL (增删改)
     *
     * @param sql
     * @return
     */
    private int executeUpdate(String sql) {
        Integer res = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            logger.debug(sql);
            res = st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 执行preparedSQL (增删改)
     *
     * @param psql
     * @param params
     * @return
     */
    private int executeUpdate(String psql, Object... params) {
        Integer res = null;
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(psql);
            for (int i = 1; i <= params.length; i++) {
                pst.setObject(i, params[i]);
            }
            logger.debug(psql.replaceAll("\\n", " ").replaceAll("\\?", "{}"), params);
            res = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 执行查询SQL,并将结果封装成 List<Map<String, Object>>
     *
     * @param sql
     * @return
     */
    private List<Map<String, Object>> getResultMapList(String sql) {
        List res = null;
        Statement statement = null;
        try {
            logger.debug(sql.replaceAll("\\n", " "));
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            res = fetchResultSet(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 执行查询SQL,并将结果封装成 List<T>
     *
     * @param sql
     * @return
     */
    private <T> List<T> getResultObjtList(String sql, Class<T> clasz) {
        List res = null;
        Statement statement = null;
        try {
            logger.debug(sql.replaceAll("\\n", " "));
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            res = fetchResultSetToObj(resultSet, clasz);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }


    /**
     * 把 ResultSet 按照clasz装载成一个对象列表 List<clasz>
     *
     * @param rs
     * @param clasz
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    private <T> List<T> fetchResultSetToObj(ResultSet rs, Class<T> clasz) throws
            SQLException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        List<T> list = new ArrayList<>();
        List<String> cls = getColumnsLabel(rs);
        while (rs.next()) {
            T t = clasz.newInstance();
            for (String c : cls) {
                Object cell = rs.getObject(c);
                if (cell != null && clasz.getDeclaredField(c) != null) {
                    clasz.getMethod(getSetMethodByColumnLabel(c), cell.getClass()).invoke(t, cell);
                }
            }
            list.add(t);
        }
        return list;
    }

    /**
     * 把 ResultSet 装载成 List<Map<String, Object>>
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> fetchResultSet(ResultSet rs) throws SQLException {
        List resMap = new ArrayList();
        List<String> cls = getColumnsLabel(rs);
        while (rs.next()) {
            Map map = new HashMap();
            for (String c : cls) {
                map.put(c, rs.getObject(c));
            }
            resMap.add(map);
        }
        return resMap;
    }

    /**
     * 获取ResultSet结果集的列名称
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List getColumnsLabel(ResultSet rs) throws SQLException {
        List<String> cls = new ArrayList<>();
        int columeCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columeCount; i++) {
            cls.add(rs.getMetaData().getColumnLabel(i));
        }
        return cls;
    }

    /**
     * 根据ResultSet结果集的列名称生成供对应domain对象调用的(getter)方法名
     * 例如: 结果集里有一列名为name,对应的getter方法名称为getName
     *
     * @param label
     * @return
     */
    private String getSetMethodByColumnLabel(String label) {
        return "set" + label.substring(0, 1).toUpperCase() + label.substring(1, label.length());
    }


    public static DBTool getInstant() {
        return getInstant(configFile);
    }

    public static DBTool getInstant(String configFile) {
        /*if (dbTool != null) return dbTool;
        else {
            try {
                synchronized (DBTool.class) {
                    dbTool = new DBTool(configFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dbTool;
        }*/
        DBTool dbTool = null;
        try {
            dbTool = new DBTool(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbTool;
    }

    public Properties getConfigProp() {
        if (prop == null) loadProp(configPath);
        return prop;
    }

    @Override
    protected void finalize() throws Throwable {
        logger.debug("Destroy DBTool!");
        super.finalize();
        conn.close();
    }

    public void close() throws SQLException {
        synchronized (this) {
            if (conn != null) {
                logger.debug("close connection");
                conn.close();
            }
        }
    }

    public static void commit() throws SQLException {
        //conn.commit();
    }


}
