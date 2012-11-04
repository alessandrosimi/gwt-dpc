package com.googlecode.gwt.dpc.client;

import java.util.ArrayList;

import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.Result;
import com.googlecode.gwt.dpc.shared.ResultOf;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DpcService</code>.
 * @author alessandro.simi@gmail.com
 */
public interface DpcServiceAsync {
	
	public <T extends Result> void call(String className, String methodName, ArrayList<String> types, ArrayList<Input> inputs, AsyncCallback<T> callback) throws DpcException;

	public void dummy(AsyncCallback<ResultOf<Integer>> callback);

}
