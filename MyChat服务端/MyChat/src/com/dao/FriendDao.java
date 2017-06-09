package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.entity.User;
import com.util.Conn;

public class FriendDao {

	public static boolean isFriend(int id1, int id2) {
		String sql = "select * from friend where user_id1=? and user_id2=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id1);
			preparedStatement.setInt(2, id2);
			ResultSet results = preparedStatement.executeQuery();
			if (results.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static boolean addFriend(int id1, int id2) {
		String sql = "insert into friend values(null,?,?)";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id1);
			preparedStatement.setInt(2, id2);
			int result = preparedStatement.executeUpdate();
			if (result != 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static List<User> getAllFriendByUserId(int userId) {
		List<User> users = new ArrayList<>();
		String sql = "select * from friend where user_id1=? or user_id2=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, userId);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				User user = new User();
				if (results.getInt("user_id1") == userId) {
					user.setId(results.getInt("user_id2"));
				} else {
					user.setId(results.getInt("user_id1"));
				}
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return users;
	}
}
