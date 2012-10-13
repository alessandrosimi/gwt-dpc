package com.googlecode.gwt.dpc.server;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>Dpc servlet that uses Spring to retrieve implementations
 * of services called from the client.</p>
 * <p>The class must be used as servlet into the <code>web.xml
 * </code> web application file descriptor where the Spring
 * listener container is defined.</p>
 * <pre>
 * &lt;listener&gt;
 *   &lt;listener-class&gt;org.springframework.web.context.ContextLoaderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;</pre>
 * <p>The servlet must be also configured into <code>web.xml</code>
 * web application file descriptor with <code>url-pattern</code>
 * listening to <b>/&lt;module&gt;/dpc</b>, where <code>module
 * </code> is the name of the Gwt module.</p>
 * <pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;myServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;com.googlecode.gwt.dpc.server.SpringDpcServlet&lt;/servlet-class&gt;
 *   &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;myServlet&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/myapplication/dpc&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;</pre>
 * <p>The service implementations should be defined into
 * the Spring configuration to allow the servlet to responce
 * to a client request when calls a service interface
 * method.</p>
 * <pre>
 * &lt;bean id="myService" class="my.application.MyServiceImpl" /&gt;</pre>
 * <p>The context should contain only one implementation of
 * the same service.</p>
 * @author alessandro.simi@gmail.com
 */
@SuppressWarnings("serial")
public class SpringDpcServlet extends DpcServlet {

	private ApplicationContext applicationContext;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	final protected <I, S extends I> S getInstance(Class<I> interfaceClass) throws ClassNotFoundException {
		Map<?,?> beans = applicationContext.getBeansOfType(interfaceClass);
		if(beans == null || beans.isEmpty()) {
			throw new ClassNotFoundException("No " + interfaceClass.getName() + " interface found in Spring context.");
		} else if(beans.size() > 1) {
			int size = beans.size();
			throw new ClassNotFoundException("Found " + size + " implementations of " + interfaceClass.getName() + " interface in Spring context.");
		} 
		return (S) beans.values().iterator().next();
	}
	
}
