package com.uitd.web.application;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public class WebApiSecurityContext implements SecurityContext {
	private String userId;

	public WebApiSecurityContext(String userId) {
		this.userId = userId;
	}

	@Override
	public String getAuthenticationScheme() {
		return "BasicAuth";
	}

	@Override
	public Principal getUserPrincipal() {
		return () -> userId;
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public boolean isUserInRole(String role) {
		return true;
	}
}