package com.uitd.web.storage.sys;

import java.util.List;
import java.util.Map;

import com.uitd.web.model.sys.Message;

public interface MessageDAL {
	/**
	 * 插入消息
	 * 
	 * @param model
	 * @return
	 */
	public boolean insert(Message model);

	/**
	 * 删除消息
	 * 
	 * @param model
	 * @return
	 */
	public boolean delete(String[] ids);

	/**
	 * 获取消息列表
	 * 
	 * @param param
	 * @return
	 */
	public List<Message> list(Map<String, Object> param);
}