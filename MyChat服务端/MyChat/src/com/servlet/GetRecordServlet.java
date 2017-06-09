package com.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.RecordDao;
import com.dao.UserDao;
import com.entity.Record;
import com.google.gson.Gson;

/**
 * Servlet implementation class GetRecordServlet
 */
@WebServlet("/GetRecordServlet")
public class GetRecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRecordServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		int userId, targetId;
		userId = UserDao.getIdByUsername(username);
		List<Record> records = RecordDao.getRecord(userId);
		for (int i = 0; i < records.size(); i++) {
			Record record = records.get(i);
			userId = Integer.parseInt(record.getUsername());
			targetId = Integer.parseInt(record.getTargetUsername());
			username = UserDao.getUsernameById(userId);
			record.setUsername(username);
			record.setName(UserDao.getNameByUsername(username));
			record.setImg(UserDao.getImageByUsername(username));
			String target = UserDao.getUsernameById(targetId);
			record.setTargetUsername(target);
			record.setTargetName(UserDao.getNameByUsername(target));
			record.setTargetImg(UserDao.getImageByUsername(target));
		}
		Gson gson = new Gson();
		System.out.println(records.toString());
		response.getWriter().write(gson.toJson(records));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
