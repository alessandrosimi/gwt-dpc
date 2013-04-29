package com.googlecode.gwt.dpc.server;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.googlecode.gwt.dpc.shared.Service;

public class GuiceServlet extends GuiceDpcServlet {

	@Override
	protected Module getGuiceModule() {
		return new AbstractModule() {
			@Override
			public void configure() {
				bind(Service.class).to(ServiceImpl.class);
			}
		};
	}

	@Override
	protected String getGwtModule() {
		return "dpc";
	}

}
