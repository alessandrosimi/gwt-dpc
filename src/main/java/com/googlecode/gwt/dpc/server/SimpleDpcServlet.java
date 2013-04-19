package com.googlecode.gwt.dpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * <p>Simple Dpc servlet storing implementations of services
 * called from the client.</p>
 * <p>The application servlet that extends this class must
 * implement the {@link #configure()} method and call to the
 * {@link #add(Class, Object)} method to map the service
 * interfaces with their implementations.</p>
 * <pre>
 * package my.application;
 * 
 * public class MyServlet extends SimpleDpcServlet {
 *   public void configure() {
 *     add(MyService.class, new MyServiceImpl());
 *   }
 * }</pre>
 * <p>The servlet must be configured into <code>web.xml</code>
 * web application file descriptor with <code>url-pattern</code>
 * listening to <b>/{@code<module>}/dpc</b>, where <code>module
 * </code> is the name of the Gwt module.</p>
 * <pre>
 * {@code
 * <servlet>
 *   <servlet-name>myServlet</servlet-name>
 *   <servlet-class>my.application.MyServlet</servlet-class>
 * </servlet>
 * 
 * <servlet-mapping>
 *   <servlet-name>myServlet</servlet-name>
 *   <url-pattern>/myapplication/dpc</url-pattern>
 * </servlet-mapping>
 * }</pre>
 * It is possible to add more than one service interface to the
 * servlet.
 * @author alessandro.simi@gmail.com
 */
@SuppressWarnings("serial")
public abstract class SimpleDpcServlet extends DpcServlet {
	
	private Map<Class<?>, Object> instances;

	public abstract void configure();
	
	@SuppressWarnings("unchecked")
	@Override
	protected final <I, S extends I> S getInstance(Class<I> interfaceClass) {
		if(instances == null) {
			instances = new HashMap<Class<?>, Object>();
			configure();
		}
		return (S) instances.get(interfaceClass);
	}

	/**
	 * <p>Adds the mapping between the service interface and its
	 * instance.</p>
	 * @param interfaceClass service interface
	 * @param instance instance of the service.
	 */
	protected final <I, S extends I> void add(Class<I> interfaceClass, S instance) {
		instances.put(interfaceClass, instance);
	}
	
	private List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
	
	@Override
	protected List<MethodInterceptor> getInterceptors() {
		return interceptors;
	}

	/**
	 * <p>Adds interceptors at the remote call to execute
	 * code before and after a service is called.</p>
	 * @param interceptor Interceptor to add.
	 */
	protected final <I extends MethodInterceptor> void addInterceptor(I interceptor) {
		if(interceptor != null) {
			interceptors.add(interceptor);
		}
	}

}
