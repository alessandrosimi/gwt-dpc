package com.googlecode.gwt.dpc.shared;

import java.io.Serializable;

public class DpcException extends Exception implements Serializable {

	private static final long serialVersionUID = 8764143385744978548L;
	
	private Throwable target;

    protected DpcException() {}
    
    public DpcException(String message) {
        super(message);
    }

    public DpcException(Throwable cause) {
        super(cause);
        this.target = cause;
    }

    public DpcException(String message, Throwable cause) {
        super(message, cause);
        this.target = cause;
    }

	public Throwable getTarget() {
		return target;
	}

	public void setTarget(Throwable cause) {
		this.target = cause;
	}
	
	public boolean hasTarget() {
		return this.target != null;
	}
	
}
