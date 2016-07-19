package com.cootoo.tool.util;

import com.sun.rowset.JdbcRowSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.JdbcRowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by larry on 16/6/29.
 */
@SuppressWarnings("unchecked")
public class DBTool {
    private static Logger logger = LoggerFactory.getLogger(DBTool.class);

    private static DBTool dbTool = null;
    private static String configFile = "dbTool.properties";
    private static Properties prop;

    private DBConfig dbConfig = new DBConfig();
    private static Connection conn = null;

    static {
        String path = DBTool.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.endsWith(File.separator)) {
            configFile = path + configFile;
        } else {
            configFile = path.substring(0, path.lastIndexOf(File.separator) + 1) + configFile;
        }
    }

    private DBTool() throws Exception {
        init();
    }

    private void init() throws Exception {
        if (dbTool != null) return;
        logger.debug("init dbTool ... ");
        if (!new File(configFile).isFile()) {
            throw new Exception("Not find the dbTool.properties in class path !");
        }
        logger.debug("configFile : " + configFile);
        if (configFile != null) loadProp();
        Class.forName("com.mysql.jdbc.Driver");
        logger.debug("Connecting : " + dbConfig.url);
        conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
        // use database
        JdbcRowSet jrs = new JdbcRowSetImpl(conn);
        jrs.setCommand("use " + dbConfig.dbname);
        jrs.execute();
    }

    private void loadProp() throws IOException {
        prop = new Properties();
        prop.load(new FileInputStream(new File(configFile)));
        //config connect properties
        dbConfig.url = prop.getProperty("url");
        dbConfig.username = prop.getProperty("username");
        dbConfig.password = prop.getProperty("password");
        dbConfig.dbname = prop.getProperty("dbname");
        if (prop.getProperty("autoCommit") != null) dbConfig.autoCommit = Boolean.getBoolean(prop.getProperty("autoCommit"));
    }

    public Map<String, Object> getOne(String sql) {
        List<Map<String, Object>> result = getResultMapList(sql);
        if (result.size() > 0) return result.get(0);
        else return null;
    }
    public <T> T getOne(String sql, Class<T> clasz) {
        List<T> result = getResultObjtList(sql, clasz);
        if (result.size() > 0) return result.get(0);
        else return null;
    }

    public List<Map<String, Object>> list(String sql) {
        return getResultMapList(sql);
    }

    public int insertInto(String sql) {
        return executeSql(sql);
    }

    public int update(String sql) {
        return executeSql(sql);
    }

    private int executeSql(String sql) {
        Integer res = null;
        Statement st = null;
        try {
            logger.debug(sql);
            st = conn.createStatement();
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

    public <T> List<T> list(Class<T> clasz) {

        return null;
    }

    public <T> List<T> list(String sql, Class<T> clasz) {
        return getResultObjtList(sql, clasz);
    }

    private <T> List<T> getResultObjtList(String sql, Class<T> clasz) {
        List res = null;
        Statement statement = null;
        try {
            logger.debug(sql);
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


    private List<Map<String, Object>> getResultMapList(String sql) {
        List res = null;
        Statement statement = null;
        try {
            logger.debug(sql);
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
     * 把 ResultSet 按照clasz装载成一个对象列表 List<clasz>
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
                if(cell != null && clasz.getDeclaredField(c) != null) {
                    clasz.getMethod(getSetMethodByColumnLabel(c), cell.getClass()).invoke(t, cell);
                }
            }
            list.add(t);
        }
        return list;
    }

    /**
     * 把 ResultSet 装载成 List<Map<String, Object>>
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

    private List getColumnsLabel(ResultSet rs) throws SQLException {
        List<String> cls = new ArrayList<>();
        int columeCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columeCount; i++) {
            cls.add(rs.getMetaData().getColumnLabel(i));
        }
        return cls;
    }

    private String getSetMethodByColumnLabel(String label) {
        return "set" + label.substring(0,1).toUpperCase() + label.substring(1, label.length());
    }

    public static DBTool getInstant() {
        if (dbTool != null) return dbTool;
        else {
            try {
                synchronized (DBTool.class) {
                    dbTool = new DBTool();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dbTool;
        }
    }

    public static Properties getConfigProp() {
        return prop;
    }

    @Override
    protected void finalize() throws Throwable {
        logger.debug("Destroy DBTool!");
        super.finalize();
        conn.close();
    }
}
