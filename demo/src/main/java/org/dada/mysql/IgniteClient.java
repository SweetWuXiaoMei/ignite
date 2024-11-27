package org.dada.mysql;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Collections;

public class IgniteClient {
    public static void main(String[] args) {
        // 配置 Ignite 客户端
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true); // 设置为客户端模式
        cfg.setIgniteInstanceName("MyIgniteClient");

        // 设置 Discovery SPI，确保与服务端一致
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1")); // 指定服务端的地址
        discoverySpi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(discoverySpi);

        // 启动 Ignite 客户端
        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.println("Ignite client connected to the cluster");

            // 获取缓存实例（名称与服务端一致）
            IgniteCache<Long, String> cache = ignite.cache("MyCache");

            // 测试 SQL 查询
//            testSqlQuery(ignite);

            // 测试缓存操作
            testCacheOperations(cache);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试缓存的增删改查操作
     */
    public static void testCacheOperations(IgniteCache<Long, String> cache) {
        System.out.println("Testing Cache Operations:");

        // 插入数据
        cache.put(1L, "Hello Ignite");
        System.out.println("Inserted: " + cache.get(1L));

        // 更新数据
        cache.put(1L, "Updated Ignite");
        System.out.println("Updated: " + cache.get(1L));

        // 删除数据
        cache.remove(1L);
        System.out.println("Deleted: " + cache.get(1L)); // 应为 null
    }

    /**
     * 测试 SQL 查询
     */
    public static void testSqlQuery(Ignite ignite) {
        System.out.println("Testing SQL Query:");

        // 创建查询
        String createTableSql = "CREATE TABLE IF NOT EXISTS String (id BIGINT PRIMARY KEY, value VARCHAR(255))";
        String insertSql = "INSERT INTO String (id, value) VALUES (1, 'SQL Ignite')";
        String selectSql = "SELECT * FROM String";
        String deleteSql = "DELETE FROM String WHERE id = 1";

        // 执行查询
        executeSqlQuery(ignite, createTableSql);
        executeSqlQuery(ignite, insertSql);

        System.out.println("Query Results After Insert:");
        executeSqlQuery(ignite, selectSql);

        executeSqlQuery(ignite, deleteSql);

        System.out.println("Query Results After Delete:");
        executeSqlQuery(ignite, selectSql);
    }

    /**
     * 执行 SQL 查询
     */
    public static void executeSqlQuery(Ignite ignite, String sql) {
        SqlFieldsQuery query = new SqlFieldsQuery(sql);

        // 查询缓存
        try (QueryCursor<List<?>> cursor = ignite.cache("MyCache").query(query)) {
            for (List<?> row : cursor) {
                System.out.println(row);
            }
        }
    }
}