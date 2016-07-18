package com.cootoo.tool.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by larry on 16/6/30.
 */
public class BeanFetchData implements FetchData {

    @Override
    public <T> T fetchRow(ResultSet rs, Method methods) {
        return null;
    }

    @Override
    public <T> List<T> fetchList(ResultSet rs, Class<T> clasz) throws SQLException {
        while (rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();
            List<String> rsColumnName = getRsColumnName(metaData);

            Method[] methods = clasz.getMethods();
            for (Method method : methods) {
                method.getName();
                method.getReturnType();
            }

        }
        return null;
    }

    private List<String> getRsColumnName(ResultSetMetaData metaData) throws SQLException {
        List<String> list = new ArrayList<>();
        int columnCount = metaData.getColumnCount();
        for (int i=1; i<=columnCount; i++) {
            String cLabel = metaData.getColumnLabel(i);
            //String cName = metaData.getColumnName(i);
            list.add(cLabel);
        }
        return list;
    }

    private <T> T getRsValue(Class<T> clasz) {
        if(clasz.equals(String.class)) {

        } else if(clasz.equals(Integer.class)) {

        } else if(clasz.equals(Long.class)) {

        } else if(clasz.equals(Double.class)) {

        } else if(clasz.equals(Date.class)) {

        } else if(clasz.equals(Float.class)) {

        } else if(clasz.equals(Character.class)) {

        } else if(clasz.equals(String.class)) {

        } else {

        }
        return null;
    }

    private String toMedthodName(String name) {
        return "get" + name.substring(0,1).toUpperCase() + name.substring(1);
    }
}
