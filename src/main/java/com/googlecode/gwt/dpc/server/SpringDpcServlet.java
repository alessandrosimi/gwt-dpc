package com.googlecode.gwt.dpc.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>Dpc servlet that uses Spring to retrieve implementations
 * of services called from the client.</p>
 * <p>The class must be used as servlet into the <code>web.xml
 * </code> web application file descriptor where the Spring
 * listener container is defined.</p>
 * <pre>
 * {@code
 * <listener>
 *   <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
 * </listener>
 * }</pre>
 * <p>The servlet must be also configured into <code>web.xml</code>
 * web application file descriptor with <code>url-pattern</code>
 * listening to <b>/{@code<module>}/dpc</b>, where <code>module
 * </code> is the name of the Gwt module.</p>
 * <pre>
 * {@code
 * <servlet>
 *   <servlet-name>myServlet</servlet-name>
 *   <servlet-class>com.googlecode.gwt.dpc.server.SpringDpcServlet</servlet-class>
 *   <load-on-startup>1</load-on-startup>
 * </servlet>
 * 
 * <servlet-mapping>
 *   <servlet-name>myServlet</servlet-name>
 *   <url-pattern>/myapplication/dpc</url-pattern>
 * </servlet-mapping>
 * }</pre>
 * <p>The service implementations should be defined into
 * the Spring configuration to allow the servlet to responce
 * to a client request when calls a service interface
 * method.</p>
 * <pre>
 * {@code
 * <bean id="myService" class="my.application.MyServiceImpl" />
 * }</pre>
 * <p>The context should contain only one implementation of
 * the same service.</p>
 * @author alessandro.simi@gmail.com
 */
@SuppressWarnings("serial")
public class SpringDpcServlet extends DpcServlet {

	private ApplicationContext applicationContext;
	
	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final <I, S extends I> S getInstance(Class<I> interfaceClass) throws ClassNotFoundException {
		Map<?,?> beans = applicationContext.getBeansOfType(interfaceClass);
		if(beans == null || beans.isEmpty()) {
			throw new ClassNotFoundException("No " + interfaceClass.getName() + " interface found in Spring context.");
		} else if(beans.size() > 1) {
			int size = beans.size();
			throw new ClassNotFoundException("Found " + size + " implementations of " + interfaceClass.getName() + " interface in Spring context. It must be one.");
		} 
		return (S) beans.values().iterator().next();
	}

	@Override
	protected final List<MethodInterceptor> getInterceptors() {
		List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
		Map<?,?> beans = applicationContext.getBeansOfType(Interceptorcontainer.class);
		if(beans != null && !beans.isEmpty()) {
			for(Object container : beans.values()) {
				interceptors.addAll(((Interceptorcontainer) container).getInterceptors());
			}
		}
		return interceptors;
	}
	
	public static class Interceptorcontainer {
		
		private List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();

		public List<MethodInterceptor> getInterceptors() {
			return interceptors;
		}

		public void setInterceptors(List<MethodInterceptor> interceptors) {
			this.interceptors = interceptors;
		}
		
	}
	
}
