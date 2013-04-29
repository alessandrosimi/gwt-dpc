package com.googlecode.gwt.dpc.shared;

import com.googlecode.gwt.dpc.shared.Dpc;

public interface Service extends Dpc {

	String uppercase(String value);
	
	long onePlus(int input);

	Integer getCount();

	void incrementCount();
	
}
