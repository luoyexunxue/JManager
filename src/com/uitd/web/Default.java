package com.uitd.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.uitd.util.Common;
import com.uitd.web.application.WebApiAuthFilter;
import com.uitd.web.application.common.ListResult;
import com.uitd.web.model.Login;
import com.uitd.web.model.User;
import com.uitd.web.service.UserService;

@WebServlet("/Default")
public class Default extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletContext sc = req.getServletContext();
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sc);
		UserService userService = ac.getBean(UserService.class);
		String ticket = req.getParameter("ticket");
		if (!StringUtils.isEmpty(ticket)) {
			String week = null;
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				week = "星期一";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
				week = "星期二";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
				week = "星期三";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
				week = "星期四";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
				week = "星期五";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				week = "星期六";
			else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				week = "星期天";
			req.setAttribute("lblDateTime", Common.toString(new Date(), "yyyy年MM月dd日") + " " + week);
			req.setAttribute("lblUsername", "");
			req.setAttribute("lblTicket", ticket);
			String userId = WebApiAuthFilter.checkAuthorized(ticket, false);
			User user = userService.item(userId);
			if (user != null) {
				req.setAttribute("lblUsername", user.getUsername());
				req.setAttribute("lblName", user.getName());
				if (user.getAvatar() != null) {
					req.setAttribute("imgAvatar",
							"data:image/png;base64," + Base64.getEncoder().encodeToString(user.getAvatar()));
				} else {
					req.setAttribute("imgAvatar", "Application/app/img/avatar.jpg");
				}
				ListResult<Login> ds = userService.history(userId, 10);
				req.setAttribute("loginRecord", ds.getRows());
				req.setAttribute("lblHasMore", ds.getTotal() > 10);
				req.getRequestDispatcher("index.jsp").forward(req, resp);
				return;
			}
			OutputStream writer = resp.getOutputStream();
			writer.write("<script>alert('身份认证失败，请重新登录')</script>".getBytes("utf-8"));
			writer.close();
			return;
		}
		Cookie cookie = null;
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie item : cookies) {
				if (item.getName().equals("ticket")) {
					cookie = item;
					break;
				}
			}
		}
		if (cookie != null && !req.getQueryString().endsWith("logout")) {
			ticket = cookie.getValue();
			String auth = WebApiAuthFilter.checkAuthorized(URLDecoder.decode(ticket, "GBK"), true);
			if (auth != null) {
				String[] info = auth.split("&", -1);
				User user = userService.item(info[0]);
				if (user != null && user.getPassword().equals(info[1])) {
					WebApiAuthFilter.updateAuthorize(info[0], info[1]);
					resp.sendRedirect("Application/index.html?ticket=" + ticket);
					return;
				}
			}
		}
		resp.sendRedirect("Application/login.html");
	}
}