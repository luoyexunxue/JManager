package com.uitd.web.application;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.uitd.push.ProxyHelper;
import com.uitd.web.application.common.DatabaseHelper;
import com.uitd.web.service.sys.MessageService;

public class Application extends ResourceConfig {
	public static ProxyHelper MessageProxy = null;

	/**
	 * 构造函数
	 */
	public Application() {
		packages("com.uitd.web.controller");

		register(RequestContextFilter.class);
		register(WebApiAuthFilter.class);
		register(LoggingFeature.class);
		register(JacksonJaxbJsonProvider.class);

		Map<String, MediaType> map = new HashMap<>();
		map.put("xml", MediaType.APPLICATION_XML_TYPE);
		map.put("json", MediaType.APPLICATION_JSON_TYPE);
		register(new UriConnegFilter(map, null));

		DatabaseHelper database = new DatabaseHelper();
		database.initDatabase();
	}

	@Autowired
	public void setProxy(MessageService service) {
		MessageProxy = new ProxyHelper(service);
	}
}