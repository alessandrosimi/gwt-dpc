package com.googlecode.gwt.dpc.server.aware;

import javax.servlet.http.HttpServletResponse;

/**
 * Implement or extends this interface in your
 * service implementation or interface to access
 * to the http servlet response.
 * @author alessandro.simi@gmail.com
 * @see HttpServletResponse
 */
public interface ResponseAware {

	public void setResponse(HttpServletResponse response);

}
