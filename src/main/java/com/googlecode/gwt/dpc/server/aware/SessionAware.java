package com.googlecode.gwt.dpc.server.aware;

import javax.servlet.http.HttpSession;

/**
 * Implement or extends this interface in your
 * service implementation or interface to access
 * to the http session.
 * @author alessandro.simi@gmail.com
 * @see HttpSession
 */
public interface SessionAware {

	public void setSession(HttpSession session);
	
}
