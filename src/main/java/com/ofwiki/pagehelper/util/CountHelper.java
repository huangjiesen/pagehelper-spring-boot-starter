package com.ofwiki.pagehelper.util;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:49.
 */
public final class CountHelper {
    private static Logger logger = LoggerFactory.getLogger(CountHelper.class);

    public CountHelper() {
    }

    public static void getCount(Connection connection, String sql, Object parameterObject, BoundSql countBoundSql, MappedStatement ms) throws Exception {
        DefaultParameterHandler handler = new DefaultParameterHandler(ms, parameterObject, countBoundSql);
        ResourceTracker tracker = new ResourceTracker("Total Count");

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            tracker.attach(ps);
            handler.setParameters(ps);
            ResultSet rs = ps.executeQuery();
            tracker.attach(rs);
            int count = 0;
            if(rs.next()) {
                count = rs.getInt(1);
            }

            if(logger.isDebugEnabled()) {
                logger.debug("Total count [{}], sql [{}]", Integer.valueOf(count), sql);
            }

            PageHelper.setTotalCount(count);
        } catch (Exception var13) {
            PageHelper.setTotalCount(0);
            throw var13;
        } finally {
            tracker.clear();
        }

    }
}
