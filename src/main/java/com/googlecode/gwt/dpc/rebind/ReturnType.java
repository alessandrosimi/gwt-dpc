package com.googlecode.gwt.dpc.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

public enum ReturnType {
	PRIMITIVE,
	CLASS;
    
    public static ReturnType find(JType returnType) {
    	JPrimitiveType primitive = returnType.isPrimitive();
    	if(primitive != null) {
    		return ReturnType.PRIMITIVE.setPrimitive(primitive);
    	} else {
    		JClassType classType = returnType.isClass();
    		if(classType != null) {
    			return ReturnType.CLASS;
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