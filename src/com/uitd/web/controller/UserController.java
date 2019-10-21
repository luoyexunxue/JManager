package com.uitd.web.controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.uitd.util.Common;
import com.uitd.web.application.WebApiAuth;
import com.uitd.web.application.WebApiController;
import com.uitd.web.application.common.BooleanResult;
import com.uitd.web.model.Login;
import com.uitd.web.model.User;
import com.uitd.web.service.UserService;

@Component
@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserController extends WebApiController {
	@Autowired
	private UserService service;

	/**
	 * 用户信息
	 * 
	 * @return
	 */
	@POST
	@WebApiAuth
	@Path("item")
	public BooleanResult<User> item() {
		BooleanResult<User> result = new BooleanResult<User>();
		result.setData(service.item(getUserID()));
		if (result.getData() != null) {
			result.setSuccess(true);
			result.setMessage("获取成功");
			result.getData().setId(null);
			result.getData().setPassword(null);
		}
		return result;
	}

	/**
	 * 登录验证
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	@POST
	@Path("login")
	public BooleanResult<String> login(@Context HttpServletRequest request, @RequestBody Map<String, Object> param) {
		String username = (String) param.get("username");
		String password = (String) param.get("password");
		Login login = new Login();
		login.setId(UUID.randomUUID().toString().replace("-", ""));
		login.setIp(getAddress(request));
		login.setPlatform(getBrowser(request));
		login.setTime(Common.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		login.setSuccess(false);
		return service.login(username, password, login);
	}

	/**
	 * 修改密码
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@Path("change")
	public BooleanResult<String> change(@RequestBody Map<String, Object> param) {
		String username = (String) param.get("username");
		String password = (String) param.get("password");
		String newpwd = (String) param.get("newpwd");
		return service.change(username, password, newpwd);
	}

	/**
	 * 修改头像
	 * 
	 * @param param
	 * @return
	 */
	@POST
	@WebApiAuth
	@Path("avatar")
	public BooleanResult<String> avatar(@RequestBody Map<String, Object> param) {
		String username = (String) param.get("username");
		String avatar = (String) param.get("avatar");
		return service.avatar(username, avatar);
	}
}