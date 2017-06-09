package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.entity.Record;
import com.util.Conn;

public class RecordDao {
	public static List<Record> getRecord(int userId) {
		List<Record> records = new ArrayList<>();
		String sql = "select * from record where user_id=? or target_id=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, userId);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				Record record = new Record();
				record.setId(results.getInt("id"));
				record.setUsername(String.valueOf(results.getInt("user_id")));
				record.setTargetUsername(String.valueOf(results.getInt("target_id")));
				record.setDesc(results.getString("desc"));
				record.setResult(results.getString("result"));
				records.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return records;
	}

	public static boolean isAddRecord(int userId, int targetId) {
		String sql = "select result from record where user_id=? and target_id=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, targetId);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				if ("add".equals(results.getString("result"))) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static boolean modifyRecord(int id, String result) {
		String sql = "update record set result=? where id=?";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, result);
			preparedStatement.setInt(2, id);
			int res = preparedStatement.executeUpdate();
			if (res != 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conn.closeConnection(conn);
		}
		return false;
	}

	public static boolean addRecord(int userId, int targetId, String desc) {
		String sql = "insert into record values(null,?,?,?,?)";
		Connection conn = Conn.getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, targetId);
			preparedStatement.setString(3, desc);
			preparedStatement.setString(4, "add");
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
}
