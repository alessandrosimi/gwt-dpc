package com.googlecode.gwt.dpc.client;

import java.util.ArrayList;

import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.Result;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * @author alessandro.simi@gmail.com
 */
@RemoteServiceRelativePath("dpc")
public interface DpcService extends RemoteService {
	public Result<?> call(String className, String methodName, ArrayList<String> types, ArrayList<Input> inputs) throws DpcException;
}
