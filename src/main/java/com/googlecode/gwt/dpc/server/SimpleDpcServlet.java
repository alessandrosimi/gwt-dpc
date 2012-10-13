package com.googlecode.gwt.dpc.server;

import java.util.HashMap;
import java.util.Map;

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
 * listening to <b>/&lt;module&gt;/dpc</b>, where <code>module
 * </code> is the name of the Gwt module.</p>
 * <pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;myServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;my.application.MyServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;myServlet&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/myapplication/dpc&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
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
	final protected <I, S extends I> S getInstance(Class<I> interfaceClass) {
		if(instances == null) {
			instances = new HashMap<Class<?>, Object>();
			configure();
		}
		return (S) instances.get(interfaceClass);
	}

	/**
	 * <p>Add the mapping between the service interface and its
	 * instance.</p>
	 * @param interfaceClass service interface
	 * @param instance instance of the service.
	 */
	final protected <I, S extends I> void add(Class<I> interfaceClass, S instance) {
		instances.put(interfaceClass, instance);
	}

}
