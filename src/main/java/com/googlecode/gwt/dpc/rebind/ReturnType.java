package com.googlecode.gwt.dpc.rebind;

import com.googlecode.gwt.dpc.shared.Result;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

public enum ReturnType {
	RESULT,
	PRIMITIVE,
	CLASS;
    
    public static ReturnType find(JType returnType) {
    	JPrimitiveType primitive = returnType.isPrimitive();
    	if(primitive != null) {
    		return ReturnType.PRIMITIVE.setPrimitive(primitive);
    	} else {
    		JClassType classType = returnType.isClass();
    		if(classType != null) {
    			// TODO Verify if the class extends indirectly Result and if it has only permitted inputs.
    			JClassType superClass = classType.getSuperclass();
    			if(superClass != null && superClass.getQualifiedSourceName().equals(Result.class.getName())) {
    				return ReturnType.RESULT;
    			} else {
    				return ReturnType.CLASS;
    			}
    		} else {
    			return null;
    		}
    	}
    }
	
	private JPrimitiveType primitive;

	public JPrimitiveType getPrimitive() {
		return primitive;
	}

	public ReturnType setPrimitive(JPrimitiveType primitive) {
		this.primitive = primitive;
		return this;
	}
	
}