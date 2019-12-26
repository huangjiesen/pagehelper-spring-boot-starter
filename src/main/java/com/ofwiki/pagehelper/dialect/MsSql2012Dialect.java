package com.ofwiki.pagehelper.dialect;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:43.
 */
public class MsSql2012Dialect extends Dialect {
    public MsSql2012Dialect() {
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
        sqlBuilder.append(PageDialectHelper.getLineSql(sql));
        sqlBuilder.append(" OFFSET ");
        sqlBuilder.append(offset);
        sqlBuilder.append(" ROWS ");
        sqlBuilder.append(" FETCH NEXT ");
        sqlBuilder.append(limit);
        sqlBuilder.append(" ROWS ONLY");
        return sqlBuilder.toString();
    }

    @Override
    public String getCountSql(String sql) {
        return PageDialectHelper.getCountString(sql);
    }
}
