package com.uitd.web.application.common;

/**
 * 布尔返回值
 * 
 * @author luoyexunxue
 *
 * @param <T>
 */
public class BooleanResult<T> {
	// 成功标志
	private boolean success;
	// 错误信息
	private String message;
	// 返回数据
	private T data;

	/**
	 * 构造1
	 */
	public BooleanResult() {
		this.success = false;
		this.message = "未知错误";
	}

	/**
	 * 构造2
	 * 
	 * @param success
	 * @param message
	 */
	public BooleanResult(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	/**
	 * 构造3
	 * 
	 * @param success
	 * @param message
	 * @param data
	 */
	public BooleanResult(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}