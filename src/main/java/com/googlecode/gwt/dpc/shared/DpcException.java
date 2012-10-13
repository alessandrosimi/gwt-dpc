package com.googlecode.gwt.dpc.shared;

import java.io.Serializable;

public class DpcException extends Exception implements Serializable {

	private static final long serialVersionUID = 8764143385744978548L;

    protected DpcException() {}
    
    public DpcException(String message) {
        super(message);
    }

    public DpcException(Throwable cause) {
        super(cause);
    }

    public DpcException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
