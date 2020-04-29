package com.uitd.push;

public class ProxyData {
	/**
	 * 消息ID(4字节)
	 */
	private int id;
	/**
	 * 命令字(4字节)
	 */
	private int command;
	/**
	 * 业务控制(4字节)
	 */
	private int control;
	/**
	 * 失效时间(4字节)
	 */
	private int expired;
	/**
	 * 时间戳(8字节)
	 */
	private long timestamp;
	/**
	 * 消息来源(32字节)
	 */
	private String source;
	/**
	 * 接收目标(32字节)
	 */
	private String target;
	/**
	 * 字符串数据
	 */
	private String data;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getControl() {
		return control;
	}

	public void setControl(int control) {
		this.control = control;
	}

	public int getExpired() {
		return expired;
	}

	public void setExpired(int expired) {
		this.expired = expired;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}