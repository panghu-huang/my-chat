package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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
 * Servlet implementation class QueryUserServlet
 */
@WebServlet("/QueryUserServlet")
public class QueryUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QueryUserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		// 要添加的好友
		String keyword = request.getParameter("keyword");
		// 自己的用户名
		String username = request.getParameter("username");
		User user = new User();
		user.setUsername(keyword);
		if (UserDao.isUser(user)) {
			// 判断是否已经是好友关系
			int id1 = UserDao.getIdByUsername(username);
			int id2 = UserDao.getIdByUsername(keyword);
			if (id1 > id2) {
				int id = id1;
				id1 = id2;
				id2 = id;
			}
			user = UserDao.getUser(user);
			Gson gson = new Gson();
			if (FriendDao.isFriend(id1, id2)) {
				user.setPassword("1");
			} else {
				user.setPassword("0");
			}
			out.write(gson.toJson(user));
		} else {
			out.write("0");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
