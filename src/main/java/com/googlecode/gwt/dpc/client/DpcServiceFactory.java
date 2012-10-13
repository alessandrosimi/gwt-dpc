package com.googlecode.gwt.dpc.client;

import com.google.gwt.core.client.GWT;

/**
 * This class create a factory of {@link DpcService}s.
 * @author alessandro.simi@gmail.com
 */
public class DpcServiceFactory {

	private static DpcServiceAsync instance = null;
	
	public static DpcServiceAsync getInstance() {
		if(instance == null) {
			instance = GWT.create(DpcService.class);
		}
		return instance;
	}

}
