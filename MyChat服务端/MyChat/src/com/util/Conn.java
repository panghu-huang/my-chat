package com.util;

import java.sql.*;

public class Conn {

	private static String jdbcName = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/mychat";
	private static String user = "root";
	private static String password = "123";

	/**
	 * 获取数据库连接
	 * 
	 * @return获取特定数据库连接
	 */
	public static Connection getConnection() {
		try {
			Class.forName(jdbcName);
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		try {
			Connection aConnection = DriverManager.getConnection(url, user,
					password);
			return aConnection;
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param 待关闭数据库连接
	 */

	public static void closeConnection(Connection aConnection) {
		try {
			if (aConnection != null) {
				aConnection.close();
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Connection aConnection = Conn.getConnection();
		Conn.closeConnection(aConnection);
		System.out.println("连接成功");
	}
}
