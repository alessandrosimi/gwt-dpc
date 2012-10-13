package com.googlecode.gwt.dpc.client;

import com.googlecode.gwt.dpc.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DpcAsyncCallback implements AsyncCallback<Result<?>>, IsSerializable {

	@Override
	public void onFailure(Throwable caught) {}

	@Override
	public void onSuccess(Result<?> result) {}

}
