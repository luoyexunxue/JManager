package com.uitd.push;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface IProxyStorage {
	/**
	 * 存储消息
	 * 
	 * @param id
	 * @param data
	 */
	public void insert(String id, ProxyData data);

	/**
	 * 删除消息
	 * 
	 * @param id
	 */
	public void delete(String id);

	/**
	 * 批量删除消息
	 * 
	 * @param ids
	 */
	public void delete(String[] ids);

	/**
	 * 获取消息
	 * 
	 * @param target
	 * @param limit
	 * @return
	 */
	public List<Pair<String, ProxyData>> list(String target, int limit);
}