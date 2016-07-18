package com.cootoo.tool.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by larry on 16/6/30.
 */
public interface FetchData {

    public <T> T fetchRow(ResultSet rs, Method methods);

    public <T> List<T> fetchList(ResultSet rs, Class<T> clasz) throws SQLException;

}
