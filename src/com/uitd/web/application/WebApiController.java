package com.uitd.web.application;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

public class WebApiController {
	/**
	 * 授权信息
	 */
	@Context
	private SecurityContext securityContext;

	/**
	 * 当前授权用户ID
	 * 
	 * @return
	 */
	public String getUserID() {
		return securityContext.getUserPrincipal().getName();
	}

	/**
	 * 浏览器信息
	 * 
	 * @return
	 */
	public String getBrowser(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		Browser browser = UserAgent.parseUserAgentString(agent).getBrowser();
		Version version = browser.getVersion(request.getHeader("User-Agent"));
		return browser.getName() + " " + version.getMajorVersion();
	}

	/**
	 * 客户端IP
	 * 
	 * @return
	 */
	public String getAddress(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if (StringUtils.isEmpty(ip))
			ip = "0.0.0.0";
		return ip;
	}
}