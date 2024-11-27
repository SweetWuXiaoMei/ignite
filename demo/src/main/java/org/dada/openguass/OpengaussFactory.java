package org.dada.openguass;

import javax.cache.configuration.Factory;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author sweetwuxiaomei@qq.com
 * @date 2024/11/25
 */
public class OpengaussFactory implements Factory<DataSource> {

  @Override
  public DataSource create() {
//     配置 HikariCP
    HikariConfig config = new HikariConfig();

    // 设置数据库连接参数
    config.setJdbcUrl("jdbc:opengauss://110.41.49.216:10000/postgres?currentSchema=class"); // 数据库URL
    config.setUsername("root"); // 数据库用户名
    config.setPassword("czd@1106403012"); // 数据库密码
    config.setDriverClassName("org.opengauss.Driver");

    // 设置连接池参数（可选，根据需要调整）
    config.setMaximumPoolSize(10); // 最大连接数
    config.setMinimumIdle(2); // 最小空闲连接数
    config.setIdleTimeout(30000); // 空闲连接超时时间（毫秒）
    config.setMaxLifetime(1800000); // 连接的最大生命周期（毫秒）
    config.setConnectionTimeout(30000); // 获取连接的超时时间（毫秒）

    // 创建数据源
    DataSource dataSource = new HikariDataSource(config);
    return dataSource;
  }
}
