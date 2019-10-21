package com.uitd.web.application.generator;

/**
 * SQL列定义
 * 
 * @author luoyexunxue
 *
 */
public class Columns {
	private String name;
	private String desc;
	private String type;
	private int len;
	private boolean notnull;
	private String def;

	public Columns(String name, String desc, String type, int len, boolean notnull, String def) {
		this.name = name;
		this.desc = desc;
		this.type = type.toLowerCase();
		this.len = len;
		this.notnull = notnull;
		this.def = def;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public boolean isNotnull() {
		return notnull;
	}

	public void setNotnull(boolean notnull) {
		this.notnull = notnull;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}
}