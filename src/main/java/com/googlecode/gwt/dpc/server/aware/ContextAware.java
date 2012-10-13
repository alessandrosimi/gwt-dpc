package com.googlecode.gwt.dpc.server.aware;

import javax.servlet.ServletContext;

/**
 * Implement or extends this interface in your
 * service implementation or interface to access
 * to the servlet context.
 * @author alessandro.simi@gmail.com
 * @see ServletContext
 */
public interface ContextAware {

	public void setContext(ServletContext context);
	
}
