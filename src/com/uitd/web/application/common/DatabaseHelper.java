package com.uitd.web.application.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.uitd.util.Common;

public class DatabaseHelper {
	private String driverClassName;
	private String url;
	private String username;
	private String password;

	/**
	 * 默认构造函数，读取配置文件
	 */
	public DatabaseHelper() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream("/config/configure.properties"));
			driverClassName = properties.getProperty("jdbc.driverClassName").trim();
			url = properties.getProperty("jdbc.url").trim();
			username = properties.getProperty("jdbc.username").trim();
			password = properties.getProperty("jdbc.password").trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化数据库
	 */
	public void initDatabase() {
		Connection connection = null;
		try {
			Class.forName(driverClassName);
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (!checkDatabase(connection)) {
			String sql = Common.readString(Common.getClassPath(null) + "app/database.sql");
			try {
				Statement statement = connection.createStatement();
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param context
	 * @param sql
	 * @return
	 */
	public boolean executeSql(ServletContext context, String sql) {
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
		SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) ac.getBean("sqlSessionFactory");
		SqlSession sqlSession = sqlSessionFactory.openSession();
		Connection connection = sqlSession.getConnection();
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
		return true;
	}

	/**
	 * 检查数据库
	 * 
	 * @return
	 */
	private boolean checkDatabase(Connection connection) {
		try {
			final String sql = "SELECT COUNT(1) FROM sys_user";
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
		}
		return false;
	}
}