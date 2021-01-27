package com.uitd.push;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.h2.util.StringUtils;

import com.google.gson.Gson;

public class ProxyDataHandler extends IoHandlerAdapter {
	private ProxyHelper helper = null;

	/**
	 * 默认构造函数
	 * 
	 * @param helper
	 */
	public ProxyDataHandler(ProxyHelper helper) {
		this.helper = helper;
	}

	/**
	 * 客户端连接
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		System.out.println("session created - " + session.getRemoteAddress().toString());
	}

	/**
	 * 客户端断开
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		helper.offline(session);
		System.out.println("session closed - " + session.getRemoteAddress().toString());
		super.sessionClosed(session);
	}

	/**
	 * 客户端消息
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		ProxyData request = (ProxyData) message;
		if (request.getCommand() == Command.REGISTER) {
			handleRegister(session, request);
			return;
		}
		String client = (String) session.getAttribute("id");
		if (client != null) {
			if (request.getCommand() == Command.MESSAGE) {
				handleMessage(session, request);
			}
		}
	}

	/**
	 * 处理注册消息
	 * 
	 * @param session
	 * @param request
	 */
	private void handleRegister(IoSession session, ProxyData request) {
		Gson gson = new Gson();
		Map<String, Object> responseData = new HashMap<String, Object>();
		boolean valid = !StringUtils.isNullOrEmpty(request.getSource()) && !"0".equals(request.getSource());
		responseData.put("success", valid);
		responseData.put("message", valid ? "" : "参数无效");
		ProxyData response = new ProxyData();
		response.setId(request.getId());
		response.setCommand(Command.REPLY);
		response.setControl(0);
		response.setExpired(0);
		response.setTimestamp(System.currentTimeMillis());
		response.setSource("0");
		response.setTarget(request.getSource());
		response.setData(gson.toJson(responseData));
		session.resumeWrite();
		session.write(response);
		if (valid) {
			session.setAttribute("id", request.getSource());
			helper.online(session);
		}
	}

	/**
	 * 处理推送消息
	 * 
	 * @param session
	 * @param request
	 */
	private void handleMessage(IoSession session, ProxyData request) {
		Gson gson = new Gson();
		Map<String, Object> responseData = new HashMap<String, Object>();
		boolean valid = !StringUtils.isNullOrEmpty(request.getTarget());
		responseData.put("success", valid);
		responseData.put("message", valid ? "" : "参数无效");
		ProxyData response = new ProxyData();
		response.setId(request.getId());
		response.setCommand(Command.REPLY);
		response.setControl(0);
		response.setExpired(0);
		response.setTimestamp(System.currentTimeMillis());
		response.setSource("0");
		response.setTarget(request.getSource());
		response.setData(gson.toJson(responseData));
		session.resumeWrite();
		session.write(response);
		if (valid) {
			String client = (String) session.getAttribute("id");
			response = new ProxyData();
			response.setId(request.getId());
			response.setCommand(request.getCommand());
			response.setControl(request.getControl());
			response.setExpired(request.getExpired());
			response.setTimestamp(request.getTimestamp());
			response.setSource(client);
			response.setTarget(request.getTarget());
			response.setData(request.getData());
			helper.sendMessage(response);
		}
	}
}