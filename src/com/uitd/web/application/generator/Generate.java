package com.uitd.web.application.generator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uitd.util.Common;
import com.uitd.web.Default;
import com.uitd.web.application.common.DatabaseHelper;

@WebServlet("/Generate")
public class Generate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * 调试模式
	 */
	private boolean DebugMode = false;
	/**
	 * 命名空间
	 */
	private String Namespace = null;
	/**
	 * 项目路径
	 */
	private String ProjectDir = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/config/configure.properties"));
		ProjectDir = properties.getProperty("project").trim();
		if (!(ProjectDir.endsWith("/") || ProjectDir.endsWith("\\"))) {
			ProjectDir += "/";
		}
		Namespace = Default.class.getPackage().getName();
		Map<String, String> param = new HashMap<String, String>();
		param.put("test", req.getParameter("test"));
		param.put("title", req.getParameter("title"));
		param.put("prefix", req.getParameter("prefix"));
		param.put("name", req.getParameter("name"));
		param.put("search", req.getParameter("search"));
		param.put("column", req.getParameter("column"));
		param.put("method_insert", req.getParameter("method_insert"));
		param.put("method_update", req.getParameter("method_update"));
		param.put("method_delete", req.getParameter("method_delete"));
		param.put("method_table", req.getParameter("method_table"));
		String result = generate(param, req.getServletContext());
		OutputStream writer = resp.getOutputStream();
		writer.write(result.getBytes("utf-8"));
		writer.close();
	}

	/**
	 * 生成模块
	 * 
	 * @param param
	 * @return
	 */
	public String generate(Map<String, String> param, ServletContext context) {
		String test = param.get("test");
		String title = param.get("title");
		String prefix = param.get("prefix") == null ? "sys" : param.get("prefix");
		String name = param.get("name");
		String search = param.get("search") == null ? "name" : param.get("search");
		String column = param.get("column");
		boolean method_insert = Boolean.parseBoolean(param.get("method_insert"));
		boolean method_update = Boolean.parseBoolean(param.get("method_update"));
		boolean method_delete = Boolean.parseBoolean(param.get("method_delete"));
		boolean method_table = Boolean.parseBoolean(param.get("method_table"));
		if (StringUtils.isEmpty(name)) {
			return "参数错误!";
		}
		name = Common.uppercaseFirst(name);
		List<Columns> columns = new ArrayList<Columns>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> jarray = mapper.readValue(column, List.class);
			for (Map<String, Object> item : jarray) {
				String col_name = item.get("name").toString();
				String col_desc = item.get("desc").toString();
				String col_type = item.get("type").toString();
				int col_len = Integer.parseInt(item.get("length").toString());
				boolean col_null = Boolean.parseBoolean(item.get("null").toString());
				String col_def = item.get("default").toString();
				if (col_name.toLowerCase().equals("id")) {
					throw new Exception("column 'id' has already exists");
				}
				columns.add(new Columns(col_name, col_desc, col_type, col_len, col_null, col_def));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		DebugMode = StringUtils.isEmpty(test) == false;
		boolean success1 = generateSQL(name, columns, prefix, context);
		boolean success2 = generateModel(name, columns);
		boolean success3 = generateController(name, columns, method_insert, method_update, method_delete, method_table);
		boolean success4 = generateService(name, columns);
		boolean success5 = generateStorage(name, columns, prefix);
		boolean success6 = generateMapper(name, columns, prefix, search);
		boolean success7 = generateHTML(name, columns, title, method_insert, method_update, method_delete);
		boolean success8 = updateEntry(name, title);
		StringBuilder result = new StringBuilder();
		result.append("项目路径配置：").append(ProjectDir).append("<br/>");
		result.append("生成 SQL 脚本... ").append(success1 ? "成功" : "失败").append("<br/>");
		result.append("生成 Model... ").append(success2 ? "成功" : "失败").append("<br/>");
		result.append("生成 Controller... ").append(success3 ? "成功" : "失败").append("<br/>");
		result.append("生成 Service... ").append(success4 ? "成功" : "失败").append("<br/>");
		result.append("生成 Storage... ").append(success5 ? "成功" : "失败").append("<br/>");
		result.append("生成 Mapper... ").append(success6 ? "成功" : "失败").append("<br/>");
		result.append("生成 HTML... ").append(success7 ? "成功" : "失败").append("<br/>");
		result.append("增加管理入口... ").append(success8 ? "成功" : "失败").append("<br/>");
		return result.toString();
	}

	/**
	 * 生成SQL文件
	 * 
	 * @param name
	 * @param columns
	 * @param prefix
	 * @param context
	 * @return
	 */
	private boolean generateSQL(String name, List<Columns> columns, String prefix, ServletContext context) {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> param_columns = new ArrayList<Map<String, Object>>();
		param.put("prefix", prefix);
		param.put("name", name.toLowerCase());
		param.put("columns", param_columns);
		for (Columns item : columns) {
			String type = item.getType();
			if (StringUtils.isEmpty(item.getDef()) || item.getDef().equals("null"))
				item.setDef(null);
			if (item.getDef() != null) {
				if (item.getType().equals("blob-img") || item.getType().equals("blob-file"))
					item.setDef(null);
				if (item.getType().equals("string") || item.getType().equals("date")
						|| item.getType().equals("datetime"))
					item.setDef("'" + item.getDef() + "'");
			}
			String nullable = item.isNotnull() ? "not null"
					: String.format("default %s", item.getDef() == null ? "null" : item.getDef());
			if (item.getType().equals("string"))
				type = String.format("%s(%d)", item.getLen() <= 65535 ? "varchar" : "longtext", item.getLen());
			else if (item.getType().equals("blob-img") || item.getType().equals("blob-file"))
				type = "blob";
			Map<String, Object> param_columns_item = new HashMap<String, Object>();
			param_columns_item.put("name", item.getName());
			param_columns_item.put("type", type);
			param_columns_item.put("nullable", nullable);
			param_columns.add(param_columns_item);
		}
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/database.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir + "resources/app/database.sql";
		String content = Common.readString(file);
		int index = content.indexOf("insert into");
		StringBuilder buffer = new StringBuilder();
		buffer.append(content.substring(0, index));
		buffer.append(source);
		buffer.append(content.substring(index));
		if (!DebugMode) {
			DatabaseHelper database = new DatabaseHelper();
			database.executeSql(context, source);
			Common.writeString(file, buffer.toString());
		}
		return true;
	}

	/**
	 * 生成Model
	 * 
	 * @param name
	 * @param columns
	 * @return
	 */
	private boolean generateModel(String name, List<Columns> columns) {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> param_columns = new ArrayList<Map<String, Object>>();
		param.put("package", Namespace);
		param.put("name", name);
		param.put("columns", param_columns);
		for (Columns item : columns) {
			String type = null;
			if (item.getType().equals("date") || item.getType().equals("datetime") || item.getType().equals("string")
					|| item.getType().equals("decimal")) {
				type = "String";
			} else if (item.getType().equals("blob-img") || item.getType().equals("blob-file")) {
				type = "byte[]";
			} else {
				type = item.getType();
			}
			Map<String, Object> param_columns_item = new HashMap<String, Object>();
			param_columns_item.put("name", item.getName());
			param_columns_item.put("nameU", Common.uppercaseFirst(item.getName()));
			param_columns_item.put("desc", item.getDesc());
			param_columns_item.put("type", type);
			param_columns.add(param_columns_item);
		}
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/model.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir + String.format("src/%s/model/%s.java", Namespace.replace('.', '/'), name);
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 生成Controller
	 * 
	 * @param name
	 * @param columns
	 * @param insert
	 * @param update
	 * @param delete
	 * @param table
	 * @return
	 */
	private boolean generateController(String name, List<Columns> columns, boolean insert, boolean update,
			boolean delete, boolean table) {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> param_columns = new ArrayList<Map<String, Object>>();
		param.put("package", Namespace);
		param.put("name", name);
		param.put("nameL", Common.lowercaseFirst(name));
		param.put("columns", param_columns);
		param.put("insert", insert);
		param.put("update", update);
		param.put("delete", delete);
		param.put("table", table);
		for (Columns item : columns) {
			boolean isBlob = false;
			boolean isInt = false;
			boolean isFloat = false;
			boolean isString = false;
			if (item.getType().equals("blob-img") || item.getType().equals("blob-file")) {
				isBlob = true;
				param.put("haveBase64", true);
				param.put("haveStringUtils", true);
			} else if (item.getType().equals("int")) {
				isInt = true;
			} else if (item.getType().equals("float")) {
				isFloat = true;
			} else {
				isString = true;
			}
			Map<String, Object> param_columns_item = new HashMap<String, Object>();
			param_columns_item.put("name", item.getName());
			param_columns_item.put("nameU", Common.uppercaseFirst(item.getName()));
			param_columns_item.put("isBlob", isBlob);
			param_columns_item.put("isInt", isInt);
			param_columns_item.put("isFloat", isFloat);
			param_columns_item.put("isString", isString);
			param_columns.add(param_columns_item);
		}
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/controller.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir
				+ String.format("src/%s/controller/%sController.java", Namespace.replace('.', '/'), name);
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 生成Service
	 * 
	 * @param name
	 * @param columns
	 * @return
	 */
	private boolean generateService(String name, List<Columns> columns) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("package", Namespace);
		param.put("name", name);
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/service.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir + String.format("src/%s/service/%sService.java", Namespace.replace('.', '/'), name);
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 生成Storage
	 * 
	 * @param name
	 * @param columns
	 * @param prefix
	 * @param search
	 * @return
	 */
	private boolean generateStorage(String name, List<Columns> columns, String prefix) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("package", Namespace);
		param.put("name", name);
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/storage.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir + String.format("src/%s/storage/%sDAL.java", Namespace.replace('.', '/'), name);
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 生成Mapper
	 * 
	 * @param name
	 * @param columns
	 * @param prefix
	 * @return
	 */
	private boolean generateMapper(String name, List<Columns> columns, String prefix, String search) {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> param_columns = new ArrayList<Map<String, Object>>();
		param.put("package", Namespace);
		param.put("name", name);
		param.put("prefix", prefix);
		param.put("nameL", Common.lowercaseFirst(name));
		param.put("search", search);
		param.put("columns", param_columns);
		for (Columns item : columns) {
			Map<String, Object> param_columns_item = new HashMap<String, Object>();
			param_columns_item.put("name", item.getName());
			param_columns.add(param_columns_item);
		}
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/mapper.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir
				+ String.format("resources/resource/sqlmap/%s-%s.xml", prefix, Common.lowercaseFirst(name));
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 生成HTML
	 * 
	 * @param name
	 * @param columns
	 * @param title
	 * @param insert
	 * @param update
	 * @param delete
	 * @return
	 */
	private boolean generateHTML(String name, List<Columns> columns, String title, boolean insert, boolean update,
			boolean delete) {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> param_columns = new ArrayList<Map<String, Object>>();
		param.put("title", title);
		param.put("nameL", Common.lowercaseFirst(name));
		param.put("insert", insert);
		param.put("update", update);
		param.put("delete", delete);
		param.put("actionFormatter", delete ? (update ? "1" : "2") : (update ? "3" : "4"));
		param.put("columns", param_columns);
		for (Columns item : columns) {
			String extra = "";
			if (item.getType().equals("date") || item.getType().equals("datetime"))
				extra = String.format(" formatter=\"formatter_%s\"", item.getName());
			else if (item.getType().equals("blob-img"))
				extra = String.format(" file=\"image/*\" formatter=\"formatter_%s_input\"", item.getName());
			else if (item.getType().equals("blob-file"))
				extra = String.format(" file=\"*.*\" formatter=\"formatter_%s\"", item.getName());
			String validate = "";
			if (item.isNotnull())
				validate += "required:true;";
			if (item.getType().equals("int"))
				validate += "integer:true;";
			else if (item.getType().equals("float"))
				validate += "decimal:true;";
			else if (item.getType().equals("decimal"))
				validate += "decimal:true;";
			else if (item.getType().equals("date"))
				validate += "date:true;";
			else if (item.getType().equals("datetime"))
				validate += "datetime:true;";
			if (!StringUtils.isEmpty(item.getDef()))
				extra += " default=\"" + Common.trim(item.getDef(), '\'') + "\"";
			if (!StringUtils.isEmpty(validate))
				extra += " validate=\"" + validate + "\"";
			Map<String, Object> param_columns_item = new HashMap<String, Object>();
			param_columns_item.put("date", item.getType().equals("date"));
			param_columns_item.put("datetime", item.getType().equals("datetime"));
			param_columns_item.put("blob-file", item.getType().equals("blob-file"));
			param_columns_item.put("blob-img", item.getType().equals("blob-img"));
			param_columns_item.put("name", item.getName());
			param_columns_item.put("desc", item.getDesc());
			param_columns_item.put("extra", extra);
			param_columns.add(param_columns_item);
		}
		String template = Common.readString(Common.getClassPath(this.getClass()) + "template/html.tpl");
		String source = new TemplateEngine().execute(template, param);
		if (source == null)
			return false;
		String file = ProjectDir + String.format("WebContent/Application/mgr_%s.html", Common.lowercaseFirst(name));
		if (new File(file).exists())
			return false;
		if (!DebugMode)
			Common.writeString(file, source);
		return true;
	}

	/**
	 * 增加管理入口
	 * 
	 * @param name
	 * @param title
	 * @return
	 */
	private boolean updateEntry(String name, String title) {
		String file = ProjectDir + "WebContent/Application/data/app.json";
		String url = "mgr_" + name.toLowerCase() + ".html";
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put("title", title);
		entry.put("icon", "zmdi-star");
		entry.put("url", url);
		entry.put("isOpenTab", true);
		ObjectMapper mapper = new ObjectMapper();
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> appconfig = mapper.readValue(Common.readString(file), Map.class);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> menus = (List<Map<String, Object>>) appconfig.get("menus");
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> childer = (List<Map<String, Object>>) menus.get(menus.size() - 1).get("childer");
			childer.removeIf(item -> {
				return item.containsKey("url") ? item.get("url").equals(url) : false;
			});
			childer.add(entry);
			if (!DebugMode) {
				return Common.writeString(file, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(appconfig));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}