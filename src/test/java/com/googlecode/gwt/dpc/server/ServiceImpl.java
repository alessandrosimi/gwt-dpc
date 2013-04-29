package com.googlecode.gwt.dpc.server;

import javax.servlet.http.HttpSession;

import com.googlecode.gwt.dpc.server.aware.SessionAware;
import com.googlecode.gwt.dpc.shared.Service;

public class ServiceImpl implements Service, SessionAware {
	
	private HttpSession session;
	
	@Override
	public void setSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public String uppercase(String value) {
		return value.toUpperCase();
	}

	@Override
	public long onePlus(int number) {
		return Long.valueOf(number + 1);
	}

	private static final String COUNT = "counter";
	
	@Override
	public Integer getCount() {
		Integer count = (Integer) session.getAttribute(COUNT);
		return count;
	}
	
	@Override
	public void incrementCount() {
		Integer count = (Integer) session.getAttribute(COUNT);
		if(count == null) {
			count = 1;
		} else {
			count++;
		}
		session.setAttribute(COUNT, count);
	}
	
}
