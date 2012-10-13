package com.googlecode.gwt.dpc.client;

import java.util.ArrayList;

import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DpcService</code>.
 * @author alessandro.simi@gmail.com
 */
public interface DpcServiceAsync {
	public <T extends Result<T>> void call(String className, String methodName, ArrayList<String> types, ArrayList<Input> inputs, AsyncCallback<T> callback) throws DpcException;
}
