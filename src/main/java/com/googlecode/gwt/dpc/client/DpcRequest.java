package com.googlecode.gwt.dpc.client;

import java.util.ArrayList;

import com.googlecode.gwt.dpc.shared.Input;

public class DpcRequest {

	private Integer id;
	private String className;
	private String methodName;
	private ArrayList<String> types;
	private ArrayList<Input> inputs;
	
	public DpcRequest(Integer id, String className, String methodName, ArrayList<String> types, ArrayList<Input> inputs) {
		this.id = id;
		this.className = className;
		this.methodName = methodName;
		this.types = types;
		this.inputs = inputs;
	}

	public Integer getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public ArrayList<String> getTypes() {
		return types;
	}

	public ArrayList<Input> getInputs() {
		return inputs;
	}
	
}
