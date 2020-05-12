package com.uitd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HTTP {
	/**
	 * HTTP Post
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpPost(String url, String param) {
		OutputStream out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setRequestProperty("accept", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = conn.getOutputStream();
			out.write(param.getBytes("utf-8"));
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * HTTP Get
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpGet(String url) {
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setRequestProperty("accept", "application/json");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoInput(true);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}
}