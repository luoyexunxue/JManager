package com.uitd.web.application.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表返回值
 * 
 * @author luoyexunxue
 *
 * @param <T>
 */
public class ListResult<T> {
	// 总记录数
	private int total;
	// 返回的数据列表
	private List<T> rows;

	/**
	 * 构造1
	 */
	public ListResult() {
		this.total = 0;
		this.rows = new ArrayList<T>();
	}

	/**
	 * 构造2
	 * 
	 * @param total
	 */
	public ListResult(int total) {
		this.total = total;
		this.rows = new ArrayList<T>(total);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}