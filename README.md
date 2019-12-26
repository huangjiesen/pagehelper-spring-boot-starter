# 项目介绍
MyBatis分页插件

# 使用说明
1. 添加依赖
    ```yaml
    <dependency>
        <groupId>com.ofwiki</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>0.0.1</version>
    </dependency>
    ```
1. 代码示例
    ```java
    @Service
    public class OrderService {
           // order service 方法
           public Page<OrderDTO> list(OrderListReq req) {
               Page<OrderDTO> page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
               page.setDataList(orderDao.list(req));
               return page;
           }
    }
    ```

