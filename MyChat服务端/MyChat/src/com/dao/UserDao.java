package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.entity.User;
import com.util.Conn;
import com.util.DateUtil;

public class UserDao {
	public static boolean isUser(User user) {
		String sql = "select * from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user.getUsername());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static boolean addUser(User user) {
		String sql = "insert into user values(null,?,?,?,?,?,?,null)";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setString(2, user.getPassword());
			preparedStatement.setString(3, user.getName());
			preparedStatement.setString(4, "img/ic_logo.png");
			preparedStatement.setString(5, DateUtil.getDate());
			preparedStatement.setString(6, DateUtil.getDate());
			int resultSet = preparedStatement.executeUpdate();
			if (resultSet == 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			Conn.closeConnection(conn);
		}
		return true;
	}

	public static boolean userLogin(String username) {
		String sql = "update user set login_time=?,status=? where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, DateUtil.getDate());
			preparedStatement.setString(2, "online");
			preparedStatement.setString(3, username);
			int resultSet = preparedStatement.executeUpdate();
			if (resultSet == 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			Conn.closeConnection(conn);
		}
		return true;
	}

	public static boolean userLogout(String username) {
		String sql = "update user set status=? where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, "unOnline");
			preparedStatement.setString(2, username);
			int resultSet = preparedStatement.executeUpdate();
			if (resultSet == 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			Conn.closeConnection(conn);
		}
		return true;
	}

	public static boolean isPassword(User user) {
		String sql = "select password from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user.getUsername());
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				if (results.getString("password").equals(user.getPassword())) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static User getUser(User user) {
		String sql = "select * from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, user.getUsername());
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				user.setId(results.getInt("id"));
				user.setPassword(results.getString("password"));
				user.setPhone(results.getString("phone"));
				user.setImage(results.getString("image"));
				user.setName(results.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return user;
	}

	public static int getIdByUsername(String username) {
		int id = 0;
		String sql = "select id from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				id = results.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return id;
	}

	public static String getImageByUsername(String username) {
		String image = null;
		String sql = "select image from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				image = results.getString("image");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return image;
	}

	public static String getUsernameById(int id) {
		String username = null;
		String sql = "select username from user where id=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				username = results.getString("username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return username;
	}

	public static String getNameByUsername(String username) {
		String name = null;
		String sql = "select name from user where username=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				name = results.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return name;
	}
}
