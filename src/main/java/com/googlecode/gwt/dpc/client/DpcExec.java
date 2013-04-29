package com.googlecode.gwt.dpc.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Result;
import com.googlecode.gwt.dpc.shared.ResultOf;

public class DpcExec {
	
	private static Map<Integer, DpcRequest> requests = new HashMap<Integer, DpcRequest>();

	public final static <T> void async(T service, final AsyncCallback<T> callback) {
		if(service != null) {
			// Get Request
			int id = System.identityHashCode(service);
			DpcRequest request = getRequest(id);
			// Send Request
			if(sendRequest(request, callback)) {
				removeRequest(id);
			}
		} else {
			if(callback != null) callback.onFailure(new Throwable("Null service."));
		}
	}
	
	public final static <T> boolean sendRequest(DpcRequest request, final AsyncCallback<T> callback) {
		boolean result = false;
		if(request != null) {
			result = true;
			try {
				// Call Server
				DpcServiceFactory.getInstance().call(
					request.getClassName(),
					request.getMethodName(),
					request.getTypes(),
					request.getInputs(),
					new AsyncCallback<Result>() {
						@Override
						public void onFailure(Throwable caught) {
							if(callback != null) {
								if(caught instanceof DpcException && ((DpcException) caught).hasTarget()) {
									callback.onFailure(((DpcException) caught).getTarget());
								} else {
									callback.onFailure(caught);
								}
							}
						}
						@Override @SuppressWarnings("unchecked")
						public void onSuccess(Result result) {
							if(result instanceof ResultOf<?> && callback != null) {
								T value = ((ResultOf<T>) result).getValue();
								callback.onSuccess(value);	
							}
						}	
					}
				);
			} catch (DpcException e) {
				if(callback != null) callback.onFailure(e);
			}
		} else {
			if(callback != null) callback.onFailure(new Throwable("Missing request."));
		}
		return result;
	}
	
	private final static synchronized DpcRequest getRequest(int id) {
		return requests.get(id);
	}
	
	public final static synchronized void setRequest(DpcRequest request) {
		DpcExec.requests.put(request.getId(), request);
	}

	private final static synchronized void removeRequest(int id) {
		requests.remove(id);
	}
}
