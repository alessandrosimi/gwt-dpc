package com.googlecode.gwt.dpc.server;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public abstract class AbstractSpringWrapperServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;
	
	private Servlet delegate = null;

	public void destroy() {
		delegate.destroy();
	}

	public ServletConfig getServletConfig() {
		return delegate.getServletConfig();
	}

	public String getServletInfo() {
		return delegate.getServletInfo();
	}

	public void init(ServletConfig config) throws ServletException {
		ServletContext servletContext = config.getServletContext();
		XmlWebApplicationContext context = new XmlWebApplicationContext() {
			@Override
			protected Resource getResourceByPath(String path) {
				return new ClassPathResource(path, getClassLoader());
			}
		};
		context.setConfigLocation(getContext());
		context.setServletContext(servletContext);
		context.refresh();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		delegate = new SpringDpcServlet();
		delegate.init(config);
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		delegate.service(req, res);
	}
	
	protected abstract String getContext();
	
}
