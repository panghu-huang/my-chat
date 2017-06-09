package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dao.UserDao;
import com.entity.Record;
import com.google.gson.Gson;

/**
 * Servlet implementation class GetUserInfoServlet
 */
@WebServlet("/GetUserInfoServlet")
public class GetUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sender = request.getParameter("sender");
		String receiver = request.getParameter("receiver");
		Record record = new Record();
		record.setUsername(sender);
		record.setName(UserDao.getNameByUsername(sender));
		record.setImg(UserDao.getImageByUsername(sender));
		record.setTargetUsername(receiver);
		record.setTargetName(UserDao.getNameByUsername(receiver));
		record.setTargetImg(UserDao.getImageByUsername(receiver));
		response.getWriter().write(new Gson().toJson(record));
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
