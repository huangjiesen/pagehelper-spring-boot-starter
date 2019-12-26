package com.ofwiki.pagehelper.interceptor;

import org.apache.ibatis.session.RowBounds;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:47.
 */
public class CountBounds extends RowBounds {
    private String countSql;
    private int count = -1;

    public CountBounds(int offset, int limit) {
        super(offset, limit);
    }

    public CountBounds(int offset, int limit, String countSQL) {
        super(offset, limit);
        this.countSql = countSQL;
    }

    public CountBounds(int offset, int limit, int count) {
        super(offset, limit);
        this.count = count;
    }

    public String getCountSql() {
        return this.countSql;
    }

    public int getCount() {
        return this.count;
    }
}
