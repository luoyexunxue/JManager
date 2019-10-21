package com.uitd.web.application.common;

/**
 * IP地理位置
 * 
 * @author luoyexunxue
 *
 */
public class IPRegion {
	// 开始地址
	private long begin;
	// 结束地址
	private long end;
	// 父级地名
	private String parent;
	// 地名
	private String address;
	// Internet 提供商
	private String isp;

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}
}