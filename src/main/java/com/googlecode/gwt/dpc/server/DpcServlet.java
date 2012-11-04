package com.googlecode.gwt.dpc.server; 

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.gwt.dpc.client.DpcService;
import com.googlecode.gwt.dpc.server.aware.ContextAware;
import com.googlecode.gwt.dpc.server.aware.RequestAware;
import com.googlecode.gwt.dpc.server.aware.ResponseAware;
import com.googlecode.gwt.dpc.server.aware.SessionAware;
import com.googlecode.gwt.dpc.shared.DpcException;
import com.googlecode.gwt.dpc.shared.Input;
import com.googlecode.gwt.dpc.shared.InputOf;
import com.googlecode.gwt.dpc.shared.Result;
import com.googlecode.gwt.dpc.shared.ResultOf;

/**
 * The server side implementation of the RPC service
 * defined by {@link DpcService} interface.<br/>
 * This class is used as unique access point of 
 * the server from the DPCs and invokes the method
 * indicated by the client.
 * @see DpcService
 * @author alessandro.simi@gmail.com
 */
@SuppressWarnings("serial")
public abstract class DpcServlet extends RemoteServiceServlet implements DpcService {
	
	/**
	 * <p>The method implementation of the {@link DpcService}
	 * interface and calls the method specified into the
	 * arguments with the proper inputs.</p>
	 * <p>The method converts the {@code inputs} into array of
	 * {@link Object}s, the {@code types} into array of {@link
	 * Class}es, generate an instance of the service from 
	 * {@code className} and an instance of the method from the
	 * {@code methodName} in order to invoke it and return the
	 * result.</p>
	 * <p>The method manages exceptions thrown by the methos in
	 * order to propagate the error to the client.</p>
	 * @param className  class name of the service containing
	 * the method to invoke.
	 * @param methodName method of the service name to invoke.
	 * @param types list of types of the input parameters of
	 * the method to invoke.
	 * @param inputs list of values of the input parameters of
	 * the method to invoke.
	 */
	@Override
	public final Result call(String className, String methodName, ArrayList<String> types, ArrayList<Input> inputs) throws DpcException {
		try {
			Result result = null;
			Object[] arguments = getArguments(inputs);
			Class<?>[] classes = getMethodTypes(types);
			if(classes != null) {
				Object instance = getInstance(className);
				if(instance != null) {
					Method method = getMethod(instance, methodName, classes);
					if(method != null) {
						result = invokeMethod(method, instance, arguments);
					}
				}
			}
			return result;
		} catch(Throwable throwable) {
			throw getException(className, methodName, throwable);
		}
	}
	
	/**
	 * This method gets the values from the
	 * {@link #call(String, String, ArrayList, ArrayList)}
	 * method and returns an array of <code>Object[]</code>.
	 * @param inputs input parameter of RPC.
	 * @return array with input parameters.
	 */
	private Object[] getArguments(ArrayList<Input> inputs) {
		Object[] arguments = new Object[inputs.size()];
		for(int i = 0; i < inputs.size(); i++) {
			arguments[i] = ((InputOf<?>)inputs.get(i)).getValue();
		}
		return arguments;
	}
	
	/**
	 * This method generates an array of {@link Class}es
	 * from the type parameter of
	 * {@link #call(String, String, ArrayList, ArrayList)}
	 * method.
	 * @param types type parameter of RPC.
	 * @return array of {@link Class}es.
	 * @throws ClassNotFoundException throws when the
	 * string doesn't match with any classes.
	 */
	private Class<?>[] getMethodTypes(ArrayList<String> types) throws ClassNotFoundException {
		Class<?>[] classes = new Class<?>[types.size()];
		for(int i = 0; i < types.size(); i++) {
			classes[i] = Class.forName(types.get(i));
		}
		return classes;
	}
	
	/**
	 * This method returns an instance of
	 * the interface defined as input.
	 * @param interfaceClass interface class.
	 * @return instance of the interface.
	 * @throws ClassNotFoundException if the
	 * class doesn't exist.
	 */
	protected abstract <I, S extends I> S getInstance(Class<I> interfaceClass) throws ClassNotFoundException;
	
	/**
	 * Finds the service class from the
	 * name and creates an instance, trying
	 * to populate it with servlet info.
	 * @param className service class name.
	 * @return instance of the service.
	 * @throws ClassNotFoundException if the
	 * class doesn't exist.
	 */
	private Object getInstance(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		Object instance = getInstance(clazz);
		return setAwareness(instance);
	}
	
	/**
	 * Set the Servlet info if the service
	 * class implements specific interfaces.
	 * @param instance instance of the service.
	 * @return instance of the service with
	 * Servlet information.
	 */
	private Object setAwareness(Object instance) {
		if(instance != null) {
			if(instance instanceof ContextAware) {
				((ContextAware) instance).setContext(getServletContext());
			}
			if(instance instanceof RequestAware) {
				((RequestAware) instance).setRequest(getThreadLocalRequest());
			}
			if(instance instanceof ResponseAware) {
				((ResponseAware) instance).setResponse(getThreadLocalResponse());
			}
			if(instance instanceof SessionAware) {
				HttpServletRequest request = getThreadLocalRequest();
				((SessionAware) instance).setSession(request != null ? request.getSession() : null);
			}
		}
		return instance;
	}
	
	private Method getMethod(Object instance, String methodName, Class<?>[] classes) throws SecurityException {
		Method method = null;
		for(Method m : instance.getClass().getMethods()) {
			Class<?>[] parameters = m.getParameterTypes();
			if(m.getName().equals(methodName) && compareTypes(classes, parameters)) {
				method = m;
			}
		}
		return method;
	}
	
	private boolean compareTypes(Class<?>[] one, Class<?>[] two) {
		boolean result = true;
		if(one.length == two.length) {
			for(int i = 0; i < one.length; i++) {
				String o = getClassName(one[i]);
				String t = getClassName(two[i]);
				if(!o.equals(t)) result = false;
			}
		} else result = false;
		return result;
	}
	
	private String getClassName(Class<?> c) {
		String name = c.getName();
		if(c.isPrimitive()) {
			name = Primitive.getWrapOf(name);
		}
		return name;
	}
	
	private enum Primitive {
		BOOLEAN(boolean.class.getName(), Boolean.class.getName()),
		BYTE(byte.class.getName(), Byte.class.getName()),
		CHAR(char.class.getName(), Character.class.getName()),
		DOUBLE(double.class.getName(), Double.class.getName()),
		FLOAT(float.class.getName(), Float.class.getName()),
		INT(int.class.getName(), Integer.class.getName()),
		LONG(long.class.getName(), Long.class.getName()),
		SHORT(short.class.getName(), Short.class.getName()),
		VOID(void.class.getName(), Void.class.getName());
		
		private String name;
		private String wrap;
		
		private Primitive(String name, String wrap) {
			this.name = name;
			this.wrap = wrap;
		}
		
		public static String getWrapOf(String name) {
			String result = null;
			for(Primitive primitive : values()) {
				if(primitive.name.equals(name)) result = primitive.wrap;
			}
			return result;
		}
		
	}

	private Result invokeMethod(Method method, Object instance, Object[] arguments) 
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object value = method.invoke(instance, arguments);
		return (Result) ResultOf.value(value);
	}
	
	/**
	 * This method creates the {@link DpcException} to
	 * be thrown. In case of {@link InvocationTargetException}
	 * uses the original one thrown into the method of
	 * the service.
	 * @param className service class name.
	 * @param methodName service method name.
	 * @param throwable exception.
	 * @return {@link DpcException} instance.
	 */
	private DpcException getException(String className, String methodName, Throwable throwable) {
		if(throwable instanceof InvocationTargetException) {
			throwable = ((InvocationTargetException) throwable).getTargetException();
			return new DpcException(throwable);
		} else {
			String orginalMessage = throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getName();
			return new DpcException("Impossible to call " + methodName + " method (" + className + " class): " + orginalMessage, throwable);
		}
	}

	/**
	 * This method is never called by the client.
	 * It is used to register {@link ResultOf} into
	 * the serialization policy file.
	 */
	@Override
	public ResultOf<Integer> dummy() {
		return null;
	}

}
