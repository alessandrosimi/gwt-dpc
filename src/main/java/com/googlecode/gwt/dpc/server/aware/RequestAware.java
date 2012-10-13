package com.googlecode.gwt.dpc.server.aware;

import javax.servlet.http.HttpServletRequest;

/**
 * Implement or extends this interface in your
 * service implementation or interface to access
 * to the http servlet request.
 * @author alessandro.simi@gmail.com
 * @see HttpServletRequest
 */
public interface RequestAware {

	public void setRequest(HttpServletRequest request);
	
}
