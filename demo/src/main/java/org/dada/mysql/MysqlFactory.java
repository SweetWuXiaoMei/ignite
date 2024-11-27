package org.dada.mysql;

import javax.cache.configuration.Factory;
import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * @author sweetwuxiaomei@qq.com
 * @date 2024/11/25
 */
public class MysqlFactory implements Factory<DataSource> {

  @Override
  public DataSource create() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL("jdbc:mysql://localhost:3306/class");  // 替换为实际的数据库连接URL
    dataSource.setUser("root");  // 数据库用户名
    dataSource.setPassword("chenzhida");  // 数据库密码
    return dataSource;
  }
}
