package com.ofwiki.pagehelper;

import com.ofwiki.pagehelper.interceptor.PaginationInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

/**
 * @author HuangJS
 * @date 2019-09-26 3:40 下午
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PageHelperAutoConfiguration {
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addPageInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        Iterator var5 = this.sqlSessionFactoryList.iterator();

        while(var5.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)var5.next();
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            configuration.addInterceptor(interceptor);
        }
    }
}
