package com.googlecode.gwt.dpc.server;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Injector;

public class GuiceWrapperServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;

	protected Injector injector = null;
	protected Servlet delegate = null;

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
		injector = this.getInjector();
		delegate = injector.getInstance(GuiceServlet.GuiceServlet.class);
		delegate.init(config);
	}
	
	private Injector getInjector() {
		return (new GuiceServlet()).getInjector();
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		delegate.service(req, res);
	}

}
