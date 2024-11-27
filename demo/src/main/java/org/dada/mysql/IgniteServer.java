package org.dada.mysql;

import java.sql.Types;
import java.util.Collections;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.MySQLDialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import com.mysql.cj.jdbc.MysqlDataSource;

public class IgniteServer {
  public static void main(String[] args) {
    // 创建 Ignite 配置
    IgniteConfiguration cfg = new IgniteConfiguration();
    cfg.setIgniteInstanceName("MyIgniteNode");

    // 设置 Discovery SPI，指定节点发现方式为本地（localhost）
    TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
    TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
    ipFinder.setAddresses(Collections.singletonList("127.0.0.1"));
    discoverySpi.setLocalPort(47500);
    discoverySpi.setIpFinder(ipFinder);
    cfg.setDiscoverySpi(discoverySpi);

    // 配置数据库存储（JDBC）
    //    DataSource dataSource = createDataSource();
    CacheConfiguration<Long, String> cacheCfg = new CacheConfiguration<>("MyCache");
    cacheCfg.setCacheMode(CacheMode.PARTITIONED);

    JdbcType strType = new JdbcType();
    strType.setCacheName("MyCache");
    strType.setKeyType(Long.class);
    strType.setValueType(String.class);
    strType.setDatabaseTable("String");

    strType.setKeyFields(new JdbcTypeField(Types.BIGINT, "id", Integer.class, "id"));
    strType.setValueFields(new JdbcTypeField(Types.VARCHAR, "value", String.class, "value"));

    // 配置 QueryEntity 映射表
    QueryEntity queryEntity = new QueryEntity();
    queryEntity.setKeyType(Long.class.getName());
    queryEntity.setValueType(String.class.getName());

    // 设置数据库表名和字段映射
    queryEntity.setTableName("String"); // 替换为实际表名
    queryEntity.addQueryField("id", Long.class.getName(), null); // 配置主键
    queryEntity.addQueryField("value", String.class.getName(), null); // 配置值字段

    // 设置字段与数据库列的映射
    LinkedHashMap<String, String> fieldToColumnMap = new LinkedHashMap<>();
    fieldToColumnMap.put("id", "id"); // 缓存字段 -> 数据库列
    fieldToColumnMap.put("value", "value");
    queryEntity.setFields(fieldToColumnMap);

    // 将 QueryEntity 添加到缓存配置中
    cacheCfg.setQueryEntities(Collections.singletonList(queryEntity));

    // 配置 JDBC 存储
    CacheJdbcPojoStoreFactory<Long, String> storeFactory = new CacheJdbcPojoStoreFactory<>();
    //    storeFactory.setDataSource(dataSource);
    storeFactory.setDataSourceFactory(new MysqlFactory());
    // 没有合适的dialect
    storeFactory.setDialect(new MySQLDialect()); // 使用 MySQL
    // 将存储设置到缓存配置
    cacheCfg.setCacheStoreFactory(storeFactory);
    cacheCfg.setReadThrough(true);  // 启用读取时从数据库加载
    cacheCfg.setWriteThrough(true); // 启用写入时同步到数据库
    storeFactory.setTypes(strType);

    // 将缓存配置添加到 Ignite 配置
    cfg.setCacheConfiguration(cacheCfg);

    // 启动 Ignite 实例
    try (Ignite ignite = Ignition.start(cfg)) {
      System.out.println("Ignite node started: " + ignite.name());

      // 保持 Ignite 实例运行，直到手动关闭
      while (true) {
        try {
          // 模拟服务器等待客户端请求，可以在这里处理逻辑
          Thread.sleep(10000); // 让服务器持续运行
        } catch (InterruptedException e) {
          e.printStackTrace();
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 创建 DataSource，连接到 MySQL 数据库
  private static DataSource createDataSource() {

    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL("jdbc:mysql://localhost:3306/class");  // 替换为实际的数据库连接URL
    dataSource.setUser("root");  // 数据库用户名
    dataSource.setPassword("chenzhida");  // 数据库密码
    return dataSource;
  }
}