package com.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.FriendDao;
import com.dao.UserDao;
import com.entity.User;
import com.google.gson.Gson;

/**
 * Servlet implementation class GetFriendSetvlet
 */
@WebServlet("/GetFriendServlet")
public class GetFriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetFriendServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		int userId = UserDao.getIdByUsername(username);
		List<User> users = FriendDao.getAllFriendByUserId(userId);
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			user.setUsername(UserDao.getUsernameById(user.getId()));
			user.setName(UserDao.getNameByUsername(user.getUsername()));
			user.setImage(UserDao.getImageByUsername(user.getUsername()));
		}
		System.out.println("GetFriendServlet");
		response.getWriter().write(new Gson().toJson(users));
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
