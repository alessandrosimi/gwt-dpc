package com.googlecode.gwt.dpc.server;

public class SpringWrapperServlet extends AbstractSpringWrapperServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getContext() {
		return "/application-context.xml";
	}

}
