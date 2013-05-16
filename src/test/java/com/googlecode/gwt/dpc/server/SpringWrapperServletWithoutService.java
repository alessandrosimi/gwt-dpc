package com.googlecode.gwt.dpc.server;

public class SpringWrapperServletWithoutService extends AbstractSpringWrapperServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getContext() {
		return "/application-context-missing-service.xml";
	}

}
