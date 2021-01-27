package com.uitd.web.storage.sys;

import java.util.List;
import java.util.Map;

import com.uitd.web.model.sys.Login;
import com.uitd.web.model.sys.User;

public interface UserDAL {
	/**
	 * 获取用户信息
	 * 
	 * @param id
	 * @param username
	 * @return
	 */
	public User item(Map<String, Object> param);

	/**
	 * 更新用户
	 * 
	 * @param model
	 * @return
	 */
	public int update(User model);

	/**
	 * 插入登录记录
	 * 
	 * @param model
	 * @return
	 */
	public int insertHistory(Login model);

	/**
	 * 分页获取历史登录记录
	 * 
	 * @param param
	 * @return
	 */
	public List<Login> pageHistory(Map<String, Object> param);

	/**
	 * 获取历史登录记录计数
	 * 
	 * @param param
	 * @return
	 */
	public int pageHistory_count(Map<String, Object> param);
}