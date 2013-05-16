package com.googlecode.gwt.dpc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.dpc.shared.Service;

public abstract class AbstractTests extends GWTTestCase {
	
	private Service service;

	@Override
	protected final void gwtSetUp() throws Exception {
		service = GWT.create(Service.class);
	}

	private final static String NAME = "Alex";
	
	public final void testUppercase() {
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
	
	public final void testException() {
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
	
	private final static String NAME_WITH_SPACE = "Al e x";
	
	public final void testInterceptor() {
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
	
	public final void testPrimitive() {
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
	
	public final void testCounter() {
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
