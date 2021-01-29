package com.uitd.push;

import java.util.List;

public interface IProxyStorage {
	/**
	 * 存储消息
	 * 
	 * @param data
	 */
	public void put(ProxyData data);

	/**
	 * 获取消息
	 * 
	 * @param target
	 * @param time
	 * @param limit
	 * @return
	 */
	public List<ProxyData> get(String target, String time, int limit);
}