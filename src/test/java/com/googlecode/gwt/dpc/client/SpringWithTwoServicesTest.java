package com.googlecode.gwt.dpc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Service;


public class SpringWithTwoServicesTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.googlecode.gwt.dpc.SpringWithTwoServices";
	}

	private Service service;

	@Override
	protected final void gwtSetUp() throws Exception {
		service = GWT.create(Service.class);
	}
	
	public final void testException() {
		delayTestFinish(500);
		DpcExec.async(
			service.uppercase("Alex"),
			new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					assertEquals(DpcException.class, caught.getClass());
					finishTest();
				}
				@Override
				public void onSuccess(String result) {
					fail("Must fail.");
					finishTest();
				}
			}
		);
	}
	
}
