package com.uitd.web.application;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@WebApiAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class WebApiAuthFilter implements ContainerRequestFilter {
	/**
	 * AES加解密器
	 */
	private static Cipher encryptTransform = null;
	private static Cipher decryptTransform = null;
	private static Object mutex = new Object();

	/**
	 * 加密字符串
	 * 
	 * @param message
	 * @return
	 */
	public static String encryptString(String message) {
		synchronized (mutex) {
			if (encryptTransform == null) {
				byte[] key = "ZManager#2018zrf".getBytes();
				SecretKeySpec rfc = new SecretKeySpec(key, "AES");
				try {
					encryptTransform = Cipher.getInstance("AES/ECB/PKCS5Padding");
					encryptTransform.init(Cipher.ENCRYPT_MODE, rfc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				byte[] data = message.getBytes("utf-8");
				byte[] encryptStream = encryptTransform.doFinal(data);
				return Base64.getEncoder().encodeToString(encryptStream);
			} catch (Exception e) {
				return "";
			}
		}
	}

	/**
	 * 解密字符串
	 * 
	 * @param secret
	 * @return
	 */
	public static String decryptString(String secret) {
		synchronized (mutex) {
			if (decryptTransform == null) {
				byte[] key = "ZManager#2018zrf".getBytes();
				SecretKeySpec rfc = new SecretKeySpec(key, "AES");
				try {
					decryptTransform = Cipher.getInstance("AES/ECB/PKCS5Padding");
					decryptTransform.init(Cipher.DECRYPT_MODE, rfc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				byte[] data = Base64.getDecoder().decode(secret);
				byte[] decryptStream = decryptTransform.doFinal(data);
				return new String(decryptStream, "utf-8");
			} catch (Exception e) {
				return "";
			}
		}
	}

	/**
	 * 用户ID-密码信息缓存
	 */
	private static Map<String, String> authorizeCache = new HashMap<String, String>();

	/**
	 * 更新用户验证信息
	 * 
	 * @param id
	 * @param password
	 */
	public static void updateAuthorize(String id, String password) {
		synchronized (authorizeCache) {
			authorizeCache.put(id, password);
		}
	}

	/**
	 * 检查是否授权成功
	 * 
	 * @param ticket
	 * @param timeOnly
	 * @return
	 */
	public static String checkAuthorized(String ticket, boolean timeOnly) {
		String[] authInfo = decryptString(ticket).split("&", -1);
		if (authInfo.length < 3) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = null;
		try {
			time = formatter.parse(authInfo[2]);
			time.setTime(time.getTime() + WebApiConfig.ExpiredDays * 24 * 3600 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (time == null || time.getTime() < new Date().getTime())
			return null;
		if (timeOnly)
			return String.join("&", authInfo);
		if (!authorizeCache.containsKey(authInfo[0]))
			return null;
		if (!authorizeCache.get(authInfo[0]).equals(authInfo[1]))
			return null;
		return authInfo[0];
	}

	/**
	 * 身份验证
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String auth = requestContext.getHeaderString("Authorization");
		if (auth != null) {
			String[] param = auth.split(" ", 2);
			String userId = checkAuthorized(param[1], false);
			if (userId != null) {
				requestContext.setSecurityContext(new WebApiSecurityContext(userId));
				return;
			}
		}
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	}
}