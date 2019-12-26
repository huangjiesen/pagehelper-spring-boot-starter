package com.ofwiki.pagehelper.dialect;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:44.
 */
public class MySql5Dialect extends Dialect {
    public MySql5Dialect() {
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        String querySelect = PageDialectHelper.getLineSql(sql);
        return querySelect + " limit " + offset + ", " + limit;
    }

    @Override
    public String getCountSql(String sql) {
        return PageDialectHelper.getCountString(sql);
    }
}
