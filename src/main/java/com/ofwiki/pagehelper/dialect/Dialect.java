package com.ofwiki.pagehelper.dialect;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:42.
 */
public abstract class Dialect {
    public Dialect() {
    }


    /**
     * get limit string
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    public abstract String getLimitString(String sql, int offset, int limit);

    /**
     * get count sql
     * @param sql
     * @return
     */
    public abstract String getCountSql(String sql);
}
