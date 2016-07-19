package com.cootoo.tool;

import com.cootoo.tool.util.DBTool;
import org.junit.Test;

/**
 * Created by larry on 16/7/19.
 */
public class DBToolCRUDTest {
    @Test
    public void insert() {
        int i = DBTool.getInstant().insertInto("insert into tClient value(null, 'A', '1.1.1.1', 80)");
        assert i>0==true;
    }
}
