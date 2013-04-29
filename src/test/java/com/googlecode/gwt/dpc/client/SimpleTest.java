package com.googlecode.gwt.dpc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.dpc.shared.Service;

public class SimpleTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.googlecode.gwt.dpc.Simple";
	}
	
	private Service service;

	@Override
	protected void gwtSetUp() throws Exception {
		service = GWT.create(Service.class);
	}

	private String NAME = "Alex";
	
	public void testUppercase() {
		delayTestFinish(500);
		DpcExec.async(
			service.uppercase(NAME),
			new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}
				@Override
				public void onSuccess(String result) {
					assertEquals(NAME.toUpperCase(), result);
					finishTest();
				}
			}
		);
	}
	
	public void testException() {
		delayTestFinish(500);
		DpcExec.async(
			service.uppercase(null),
			new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					assertEquals(NullPointerException.class, caught.getClass());
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
	
	private String NAME_WITH_SPACE = "Al e x";
	
	public void testInterceptor() {
		delayTestFinish(500);
		DpcExec.async(
			service.uppercase(NAME_WITH_SPACE),
			new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}
				@Override
				public void onSuccess(String result) {
					assertEquals(NAME.toUpperCase(), result);
					finishTest();
				}
			}
		);
	}
	
	public void testPrimitive() {
		delayTestFinish(500);
		DpcExec.async(
			service.onePlus(2),
			new AsyncCallback<Long>() {
				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}
				@Override
				public void onSuccess(Long result) {
					assertEquals(Long.valueOf(3), result);
					finishTest();
				}
			}
		);
	}
	
	public void testCounter() {
		service.incrementCount();
		service.incrementCount();
		service.incrementCount();
		service.incrementCount();
		delayTestFinish(500);
		DpcExec.async(
			service.getCount(),
			new AsyncCallback<Integer>() {
				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}
				@Override
				public void onSuccess(Integer result) {
					assertNotNull(result);
					assertEquals(Integer.valueOf(4), result);
					finishTest();
				}
			}
		);
	}
	

}
