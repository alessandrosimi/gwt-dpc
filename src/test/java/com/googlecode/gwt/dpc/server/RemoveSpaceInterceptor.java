package com.googlecode.gwt.dpc.server;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import static org.junit.Assert.*;

public class RemoveSpaceInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		assertNotNull(invocation.getThis());
		assertNotNull(invocation.getMethod());
		assertEquals(invocation.getMethod(), invocation.getStaticPart());
		assertNotNull(invocation.getArguments());
		Object object = invocation.proceed();
		if(object instanceof String) {
			object = ((String) object).replaceAll("\\s", "");
		}
		return object;
	}

}
