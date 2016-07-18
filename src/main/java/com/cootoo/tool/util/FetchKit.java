package com.cootoo.tool.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by larry on 16/6/30.
 */
public class FetchKit {
    public static List fetchResultSet(ResultSet rs, FetchData fd) {
        try {
            return fd.fetchList(rs, User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
