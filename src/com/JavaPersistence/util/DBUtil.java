package com.JavaPersistence.util;

import java.sql.*;

/**
 * JDBC操作的工具类,加载数据库驱动,获取数据库连接
 * 
 * @author AntsMarch
 * 
 */
public class DBUtil {
	private static Connection conn = null;

	private DBUtil() {
	}

	// 
	// * 设计DBUtil类单例
	// *
	// * @author AntsMarch
	// *
	// */
	// private static class DBUtilHelper {
	// static final DBUtil dbUtil = new DBUtil();
	// }
	//
	// public static DBUtil getIntance() {
	// return DBUtilHelper.dbUtil;
	// }

	/**
	 * 加载数据库连接驱动
	 */
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return Connection conn
	 */
	public static Connection getConnection() {
		String url = "jdbc:mysql://localhost:3306/food_quarantine1?useUnicode=true&characterEncoding=utf8";
		try {
			conn = DriverManager.getConnection(url, "root", "root");
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public static void Close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
