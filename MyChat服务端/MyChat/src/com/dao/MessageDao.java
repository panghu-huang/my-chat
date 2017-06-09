package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.entity.Information;
import com.util.Conn;

public class MessageDao {

	public static List<Information> getMessage(String username) {
		List<Information> infos = new ArrayList<Information>();
		String sql = "select * from message where receiver=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				Information info = new Information();
				String sender = results.getString("sender");
				info.setSender(sender);
				info.setSenderName(UserDao.getNameByUsername(sender));
				info.setSenderImg(UserDao.getImageByUsername(sender));
				info.setReceiver(results.getString("receiver"));
				info.setType(results.getInt("type"));
				info.setMessage(results.getString("message"));
				info.setTime(results.getString("time"));
				info.setExtra(results.getString("extra"));
				infos.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return infos;
	}

	public static void removeMessage(String username) {
		String sql = "delete from message where receiver=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
	}

	public static void addMessage(Information info) {
		String sql = "insert into message values(null,?,?,?,?,?,?)";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, info.getSender());
			preparedStatement.setString(2, info.getReceiver());
			preparedStatement.setString(3, info.getTime());
			preparedStatement.setInt(4, info.getType());
			preparedStatement.setString(5, info.getMessage());
			preparedStatement.setString(6, info.getExtra());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
	}
}
