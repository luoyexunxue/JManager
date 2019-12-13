package com.uitd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.h2.util.StringUtils;

public class Common {
	/**
	 * 格式化时间
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String toString(Date date, String format) {
		if (format == null)
			format = "yyyy-MM-dd HH:mm:ss";
		DateFormat fmt = new SimpleDateFormat(format);
		return fmt.format(date);
	}

	/**
	 * 从字符串获取日期对象
	 * 
	 * @param time
	 * @param format
	 * @return
	 */
	public static Date getDate(String time, String format) {
		if (format == null)
			format = "yyyy-MM-dd HH:mm:ss";
		DateFormat fmt = new SimpleDateFormat(format);
		try {
			return fmt.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * SHA256 加密
	 * 
	 * @param message
	 * @return
	 */
	public static String sha256(String message) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(message.getBytes("UTF-8"));
			byte[] result = messageDigest.digest();
			return toHexString(result, false, false);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字节数组转十六进制字符串
	 * 
	 * @param array
	 * @return
	 */
	public static String toHexString(byte[] array, boolean prefix, boolean lowercase) {
		StringBuilder buffer = new StringBuilder();
		if (prefix) {
			buffer.append("0x");
		}
		if (lowercase) {
			for (byte item : array)
				buffer.append(String.format("%02x", 0x00FF & item));
		} else {
			for (byte item : array)
				buffer.append(String.format("%02X", 0x00FF & item));
		}
		return buffer.toString();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readString(String filename) {
		try {
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			byte[] content = new byte[(int) file.length()];
			in.read(content);
			in.close();
			return new String(content, "utf-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 写入文件内容
	 * 
	 * @param filename
	 * @param content
	 * @return
	 */
	public static boolean writeString(String filename, String content) {
		try {
			File file = new File(filename);
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes("utf-8"));
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将字符串第一个字符转换为大写输出
	 * 
	 * @param str
	 * @return
	 */
	public static String uppercaseFirst(String str) {
		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str;
	}

	/**
	 * 将字符串第一个字符转换为小写输出
	 * 
	 * @param str
	 * @return
	 */
	public static Object lowercaseFirst(String str) {
		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
		return str;
	}

	/**
	 * 字符串截断
	 * 
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String trim(String str, char ch) {
		if (str == null || str.length() == 0)
			return str;
		int begin = 0;
		int end = str.length() - 1;
		while (str.charAt(begin) == ch)
			begin++;
		while (str.charAt(end) == ch)
			end--;
		return str.substring(begin, end + 1);
	}

	/**
	 * 获取class类文件所在目录
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getClassPath(Class<?> clazz) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String filePath = loader.getResource("").getPath().replace('\\', '/');
		if (clazz != null) {
			filePath += clazz.getPackage().getName().replace(".", "/");
		}
		if (!filePath.endsWith("/")) {
			filePath += "/";
		}
		return filePath.replaceAll("%20", " ");
	}

	/**
	 * 是否合法命名
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isNameValid(String name) {
		if (StringUtils.isNullOrEmpty(name)) {
			return false;
		}
		char firstChar = name.charAt(0);
		if (firstChar < 65 || (firstChar > 90 && firstChar < 95) || (firstChar > 95 && firstChar < 97)
				|| firstChar > 122) {
			return false;
		}
		for (int i = 1; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (ch < 48 || ch > 122)
				return false;
			if (ch > 57 && ch < 65)
				return false;
			if (ch > 90 && ch < 95)
				return false;
			if (ch > 95 && ch < 97)
				return false;
		}
		return true;
	}
}