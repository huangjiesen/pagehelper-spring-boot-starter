package com.ofwiki.pagehelper.interceptor;

import com.ofwiki.pagehelper.dialect.Dialect;
import com.ofwiki.pagehelper.dialect.DialectResolver;
import com.ofwiki.pagehelper.util.CountHelper;
import com.ofwiki.pagehelper.util.Page;
import com.ofwiki.pagehelper.util.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:47.
 */

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class PaginationInterceptor extends AbstractInterceptor implements Interceptor {
    private static final String DEFAULT_SHARDING_CONFIG = "classpath:sharding-config.xml";
    private boolean shardingSwitchOn;
    private String shardingConfigLocation;

    public PaginationInterceptor() {
    }

    public void setShardingSwitchOn(boolean shardingSwitchOn) {
        this.shardingSwitchOn = shardingSwitchOn;
    }

    public void setShardingConfigLocation(String shardingConfigLocation) {
        this.shardingConfigLocation = shardingConfigLocation;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object var11;
        try {
            Connection connn = (Connection)invocation.getArgs()[0];
            String databaseName = connn.getMetaData().getDatabaseProductName();
            Dialect dialect = DialectResolver.resolve(databaseName);
            StatementHandler statementHandler = this.getStatementHandler(invocation);
            boolean delegate = this.isDelegate(statementHandler);
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            BoundSql boundSql = this.getBoundSql(delegate, metaObject);
            this.setValue(metaObject, delegate, "boundSql.sql", this.beautifySql(boundSql.getSql()));

            String sql = boundSql.getSql();
            this.loggingNonQuery(statementHandler, sql);
            Page page = PageHelper.getLocalPage();
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Page  : {}", page);
            }

            if(page != null) {
                RowBounds rb = new RowBounds(page.getStartRow(), page.getPageSize());
                this.calculateDataCount(invocation, statementHandler, delegate, metaObject, boundSql, rb, dialect);
                this.setValue(delegate, metaObject, sql, rb, dialect);
                if(this.logger.isDebugEnabled()) {
                    this.logger.debug("Pagination SQL : {}", boundSql.getSql());
                }

                Object var12 = invocation.proceed();
                return var12;
            }

            var11 = invocation.proceed();
        } finally {
            PageHelper.clearPage();
        }

        return var11;
    }

    private void calculateDataCount(Invocation inv, StatementHandler stmth, boolean delegate, MetaObject metaObject, BoundSql boundSql, RowBounds rb, Dialect dialect) throws Exception {
        String preCountSql = null;
        int dataCount = -1;
        if(rb instanceof CountBounds) {
            CountBounds cb = (CountBounds)rb;
            if(cb.getCount() > 0) {
                dataCount = cb.getCount();
            } else {
                String countSql = cb.getCountSql();
                if(countSql != null && countSql.length() > 0) {
                    countSql = this.parseCountSqlIfNeeded(delegate, metaObject, countSql);
                    preCountSql = countSql;
                }
            }
        }

        if(dataCount > 0) {
            this.counting(dataCount);
        } else {
            if(preCountSql == null) {
                preCountSql = boundSql.getSql();
            }

            this.counting(delegate, inv, stmth, metaObject, boundSql.getParameterMappings(), preCountSql, dialect);
        }

    }

    private String parseCountSqlIfNeeded(boolean delegate, MetaObject metaObject, String countSql) {
        return countSql.trim();
    }

    private void loggingNonQuery(StatementHandler statementHandler, String sql) {
        String sqlLower = sql.trim().toLowerCase();
        if(!sqlLower.startsWith("select") && !sqlLower.startsWith("declare")) {
            this.logger.debug("SQL:{}, Parameters:{}", sqlLower, statementHandler.getParameterHandler().getParameterObject());
        }
    }

    private void counting(int dataCount) {
        PageHelper.setTotalCount(dataCount);
    }

    private void counting(boolean delegate, Invocation invocation, StatementHandler statementHandler, MetaObject metaObject, List<ParameterMapping> paramMappings, String sql, Dialect dialect) throws Exception {
        Connection connection = (Connection)invocation.getArgs()[0];
        Configuration configuration = (Configuration)this.getValue(metaObject, delegate, "configuration");
        MappedStatement mappedStatement = (MappedStatement)this.getValue(metaObject, delegate, "mappedStatement");
        Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Total count sql [{}] parameters [{}] ", sql, parameterObject);
        }

        BoundSql countBoundSql = new BoundSql(configuration, sql, paramMappings, parameterObject);
        CountHelper.getCount(connection, dialect.getCountSql(sql), parameterObject, countBoundSql, mappedStatement);
    }

    private void setValue(boolean delegate, MetaObject metaObject, String sql, RowBounds rb, Dialect dialect) {
        this.setValue(metaObject, delegate, "boundSql.sql", dialect.getLimitString(sql, rb.getOffset(), rb.getLimit()));
        this.setValue(metaObject, delegate, "rowBounds.offset", Integer.valueOf(0));
        this.setValue(metaObject, delegate, "rowBounds.limit", Integer.valueOf(2147483647));
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if(properties != null) {
            this.shardingConfigLocation = properties.getProperty("shardingConfigLocation");
            this.shardingSwitchOn = Boolean.parseBoolean(properties.getProperty("shardingSwitchOn"));
            if(this.shardingSwitchOn && StringUtils.isBlank(this.shardingConfigLocation)) {
                this.shardingConfigLocation = "classpath:sharding-config.xml";
            }
        }
    }
}
