package com.googlecode.gwt.dpc.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.gwt.dpc.client.DpcService;

/**
 * <p>This class is used to create the result values
 * of service methods.<p>
 * <pre>
 * public class StringResult extends Result&lt;StringResult&gt; {
 *   private String value;
 *   
 *   public String getValue() {
 *     return value;
 *   }
 *   
 *   public void setValue(String value) {
 *     this.value = value;
 *   }
 * }</pre>
 * <p>Only the methods that return class extending {@link
 * Result} can be used as asynchronous request of
 * {@link DpcService}.</p>
 * <pre>
 * public class MyServiceImpl implements MyService {
 * 
 *   public StringResult toUpperCase(String value) {
 *     StringResult result = new StringResult();
 *     result.setValue(value.toUpperCase());
 *     return result;
 *   }
 *   
 * }</pre>
 * <p>This type of result allow to have an asynchronous
 * response in the client with the usage of {@link
 * AsyncCallback} interface.</p>
 * @author alessandro.simi@gmail.com
 * @param <T> result type wrapper.
 */
public class Result<T extends Result<T>> implements IsSerializable {
	
	private AsyncCallback<T> callback;
	
	public final void result(AsyncCallback<T> callback) {
		this.callback = callback;
	}

	public final void onSuccess(T result) {
		callback.onSuccess(result);
	}
	
	public final void onFailure(Throwable throwable) {
		if(throwable == null) throwable = new Throwable("Empty Exception");
		callback.onFailure(throwable);
	}
	
}
