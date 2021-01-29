package com.uitd.web.model.sys;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	private String id;
	private String createtime;
	private int message_id;
	private int message_command;
	private int message_control;
	private int message_expired;
	private long message_timestamp;
	private String message_source;
	private String message_target;
	private byte[] message_data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getMessage_id() {
		return message_id;
	}

	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}

	public int getMessage_command() {
		return message_command;
	}

	public void setMessage_command(int message_command) {
		this.message_command = message_command;
	}

	public int getMessage_control() {
		return message_control;
	}

	public void setMessage_control(int message_control) {
		this.message_control = message_control;
	}

	public int getMessage_expired() {
		return message_expired;
	}

	public void setMessage_expired(int message_expired) {
		this.message_expired = message_expired;
	}

	public long getMessage_timestamp() {
		return message_timestamp;
	}

	public void setMessage_timestamp(long message_timestamp) {
		this.message_timestamp = message_timestamp;
	}

	public String getMessage_source() {
		return message_source;
	}

	public void setMessage_source(String message_source) {
		this.message_source = message_source;
	}

	public String getMessage_target() {
		return message_target;
	}

	public void setMessage_target(String message_target) {
		this.message_target = message_target;
	}

	public byte[] getMessage_data() {
		return message_data;
	}

	public void setMessage_data(byte[] message_data) {
		this.message_data = message_data;
	}
}