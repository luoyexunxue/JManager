package com.uitd.web.application.generator;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.uitd.util.Common;

public class TemplateEngine {

	public String execute(String template, Map<String, Object> param) {
		StringBuffer buffer = new StringBuffer(template);
		try {
			replaceRepeat(buffer, param);
			replaceIf(buffer, param);
			replaceText(buffer, param);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buffer.toString();
	}

	/**
	 * 替换重复块
	 * 
	 * @param template
	 * @param param
	 */
	private void replaceRepeat(StringBuffer template, Map<String, Object> param) {
		final String pattern = "{{#repeat";
		int cursor = -1;
		Stack<Triple<String, String, Integer>> repeat = new Stack<Triple<String, String, Integer>>();
		while ((cursor = template.indexOf(pattern, cursor + 1)) > 0) {
			final String control = template.substring(cursor + 9, template.indexOf("}}", cursor)).trim();
			final String blockName = control.split(" ")[0];
			if (StringUtils.isEmpty(blockName)) {
				Triple<String, String, Integer> block = repeat.size() > 0 ? repeat.pop() : null;
				if (repeat.size() == 0 && block != null && param.containsKey(block.getLeft())) {
					@SuppressWarnings("unchecked")
					final List<Map<String, Object>> sub_param = (List<Map<String, Object>>) param.get(block.getLeft());
					final String sub_template = template.substring(
							trimForward(template, template.indexOf("}}", block.getRight()) + 2),
							trimBackward(template, cursor - 1));
					StringBuffer replace_template = new StringBuffer();
					for (int i = 0; i < sub_param.size(); i++) {
						Map<String, Object> item = sub_param.get(i);
						StringBuffer template_item = new StringBuffer(sub_template);
						int index1 = template_item.indexOf("{{");
						int index2 = template_item.indexOf("}}");
						while (index1 >= 0 && index2 > 0) {
							String key = template_item.substring(index1 + 2, index2).trim();
							if (key.startsWith(block.getLeft() + ".")) {
								String subkey = key.substring(key.indexOf('.') + 1);
								key = block.getLeft() + "[" + i + "]" + subkey;
								param.put(key, item.get(subkey));
								template_item.replace(index1 + 2, index2, key);
								if (subkey.equals("#")) {
									param.put(key, i + 1);
								}
							} else if (key.startsWith("#if ")) {
								String[] keys = key.substring(4).trim().split(" ");
								StringBuffer key_template = new StringBuffer();
								for (String k : keys) {
									k = k.trim();
									if (k.startsWith(block.getLeft() + ".")) {
										String subkey = k.substring(k.indexOf('.') + 1);
										key = block.getLeft() + "[" + i + "]" + subkey;
										param.put(key, item.get(subkey));
										key_template.append(key).append(" ");
									} else {
										key_template.append(k).append(" ");
									}
								}
								template_item.replace(index1 + 2, index2, "#if " + key_template.toString().trim());
							}
							index1 = template_item.indexOf("{{", index1 + 1);
							index2 = template_item.indexOf("}}", index1);
						}
						replaceRepeat(template_item, param);
						replace_template.append(template_item);
						if (i + 1 < sub_param.size()) {
							int pos = trimBackward(replace_template, replace_template.length() - 1);
							while (pos > 0 && (replace_template.charAt(pos - 1) == '\n'
									|| replace_template.charAt(pos - 1) == '\r')) {
								pos -= 1;
							}
							replace_template.insert(pos, block.getMiddle());
						}
					}
					int start = trimBackward(template, block.getRight() - 1);
					int end = trimForward(template, template.indexOf("}}", cursor) + 2);
					template.delete(start, end);
					template.insert(start, replace_template.toString());
				}
			} else {
				String separator = "";
				if (control.length() > blockName.length()) {
					String temp = control.substring(blockName.length() + 1);
					int tindex = temp.indexOf('=');
					if (tindex > 0) {
						separator = Common.trim(temp.substring(tindex + 1), '"');
					}
				}
				repeat.push(new ImmutableTriple<String, String, Integer>(blockName, separator, cursor));
			}
		}
	}

	/**
	 * 替换条件块
	 * 
	 * @param template
	 * @param param
	 */
	private void replaceIf(StringBuffer template, Map<String, Object> param) {
		final String pattern = "{{#if";
		int cursor = -1;
		Stack<Pair<String, Integer>> repeat = new Stack<Pair<String, Integer>>();
		while ((cursor = template.indexOf(pattern, cursor + 1)) > 0) {
			String blockName = template.substring(cursor + 5, template.indexOf("}}", cursor)).trim();
			if (StringUtils.isEmpty(blockName)) {
				Pair<String, Integer> block = repeat.size() > 0 ? repeat.pop() : null;
				if (repeat.size() == 0 && block != null) {
					final String sub_template = template.substring(
							trimForward(template, template.indexOf("}}", block.getRight()) + 2),
							trimBackward(template, cursor - 1));
					StringBuffer replace_template = new StringBuffer();
					StringBuffer template_item = new StringBuffer(sub_template);
					replaceIf(template_item, param);
					int start = trimBackward(template, block.getRight() - 1);
					int end = trimForward(template, template.indexOf("}}", cursor) + 2);
					cursor = start;
					template.delete(start, end);
					String[] keys = block.getLeft().split(" ");
					boolean pass = false;
					for (String k : keys) {
						k = k.trim();
						if (param.containsKey(k) && (Boolean) param.get(k)) {
							pass = true;
							break;
						}
					}
					int els = template_item.indexOf("{{#else}}");
					replace_template.append(els > 0
							? (pass ? template_item.substring(0, trimBackward(template_item, els - 1))
									: template_item.substring(trimForward(template_item, els + 9)))
							: (pass ? template_item : ""));
					template.insert(start, replace_template.toString());
				}
			} else {
				repeat.push(new ImmutablePair<String, Integer>(blockName, cursor));
			}
		}
	}

	/**
	 * 替换文本
	 * 
	 * @param template
	 * @param param
	 */
	private void replaceText(StringBuffer template, Map<String, Object> param) {
		int index1 = template.indexOf("{{");
		int index2 = template.indexOf("}}");
		while (index1 >= 0 && index2 > 0) {
			while (template.charAt(index1 + 2) == '{') {
				index1 += 1;
			}
			String key = template.substring(index1 + 2, index2).trim();
			String replace = param.containsKey(key) ? param.get(key).toString() : "";
			template.replace(index1, index2 + 2, replace);
			index1 = template.indexOf("{{");
			index2 = template.indexOf("}}");
		}
	}

	/**
	 * 前进游标，直到遇到非空字符或者\n结束
	 * 
	 * @param template
	 * @param cursor
	 * @return
	 */
	private int trimForward(StringBuffer template, int cursor) {
		while (cursor < template.length()) {
			char ch = template.charAt(cursor);
			if ((ch > 0x20 && ch < 0x7F) || ch == '\n') {
				cursor += ch == '\n' ? 1 : 0;
				break;
			}
			cursor += 1;
		}
		return cursor;
	}

	/**
	 * 回退游标，直到遇到非空字符或者\n结束
	 * 
	 * @param template
	 * @param cursor
	 * @return
	 */
	private int trimBackward(StringBuffer template, int cursor) {
		while (cursor > 0) {
			char ch = template.charAt(cursor);
			if ((ch > 0x20 && ch < 0x7F) || ch == '\n') {
				cursor += 1;
				break;
			}
			cursor -= 1;
		}
		return cursor;
	}
}