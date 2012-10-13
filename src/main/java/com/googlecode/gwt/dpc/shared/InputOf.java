package com.googlecode.gwt.dpc.shared;

import com.googlecode.gwt.dpc.client.DpcService;

/**
 * <p>This class is used to mark a type into
 * {@link DpcService} communication. This is
 * a way to allow the compile to serialize
 * all the types of input of the service. This
 * class extends the {@link Input} class.</p>
 * @author alessandro.simi@gmail.com
 * @param <T> the input type.
 */
public class InputOf<T> extends Input {

	private T value;

	public T getValue() {
		return value;
	}

	public InputOf<T> setValue(T value) {
		this.value = value;
		return this;
	}
	
}
