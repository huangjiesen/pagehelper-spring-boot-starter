package com.ofwiki.pagehelper.interceptor;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:46.
 */
public abstract class AbstractInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final String DELEGATE = "delegate.";
    protected static final String MAPPED_STATEMENT = "mappedStatement";
    protected static final String CONFIGURATION = "configuration";
    protected static final String ROW_BOUNDS = "rowBounds";
    protected static final String BOUND_SQL = "boundSql";
    protected static final String BOUND_SQL_SQL = "boundSql.sql";
    protected static final String ROW_BOUNDS_LIMIT = "rowBounds.limit";
    protected static final String ROW_BOUNDS_OFFSET = "rowBounds.offset";
    protected static final Map<String, String> MAP = new HashMap();

    public AbstractInterceptor() {
    }

    protected StatementHandler getStatementHandler(Invocation invocation) throws Exception {
        StatementHandler target;
        Plugin plugin;
        Field field;
        for(target = (StatementHandler)invocation.getTarget(); Proxy.isProxyClass(target.getClass()); target = (StatementHandler)field.get(plugin)) {
            plugin = (Plugin)Proxy.getInvocationHandler(target);
            field = plugin.getClass().getDeclaredField("target");
            field.setAccessible(true);
        }

        return target;
    }

    protected <T> T getValue(MetaObject metaObject, boolean delegate, String key) {
        return (T)metaObject.getValue(this.key(delegate, key));
    }

    protected void setValue(MetaObject metaObject, boolean delegate, String key, Object value) {
        metaObject.setValue(this.key(delegate, key), value);
    }

    private String key(boolean delegate, String key) {
        return delegate?(String)MAP.get(key):key;
    }

    protected String beautifySql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    protected BoundSql getBoundSql(boolean delegate, MetaObject metaObject) {
        return (BoundSql)this.getValue(metaObject, delegate, "boundSql");
    }

    protected boolean isDelegate(StatementHandler statementHandler) {
        return statementHandler instanceof RoutingStatementHandler;
    }

    static {
        MAP.put("mappedStatement", "delegate.mappedStatement");
        MAP.put("configuration", "delegate.configuration");
        MAP.put("rowBounds", "delegate.rowBounds");
        MAP.put("boundSql", "delegate.boundSql");
        MAP.put("boundSql.sql", "delegate.boundSql.sql");
        MAP.put("rowBounds.limit", "delegate.rowBounds.limit");
        MAP.put("rowBounds.offset", "delegate.rowBounds.offset");
    }
}
