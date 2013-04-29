package com.googlecode.gwt.dpc.server;

import com.googlecode.gwt.dpc.shared.Service;

@SuppressWarnings("serial")
public class SimpleServlet extends SimpleDpcServlet {

	@Override
	public void configure() {
		addInterceptor(null);
		addInterceptor(new RemoveSpaceInterceptor());
		add(Service.class, new ServiceImpl());
	}

}
