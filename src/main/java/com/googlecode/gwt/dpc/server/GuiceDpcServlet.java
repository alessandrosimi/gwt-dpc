package com.googlecode.gwt.dpc.server;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;

/**
 * <p>Dpc servlet that uses Guice to retrieve implementations
 * of services called from the client.</p>
 * <p>The context listener that extends this class must
 * implement the {@link #getGwtModule()} method, specifying the
 * application module, and the {@link #getGuiceModule()} method
 * that must return the Guice configuration module(s).</p>
 * <pre>
 * package my.application;
 * 
 * public class MyContextListener extends GuiceDpcServlet {
 *   public String getGwtModule() {
 *     return "myapplication";
 *   }
 *   public Module getGuiceModule() {
 *     return new MyGuiceModule();
 *   }
 * }</pre>
 * <p>One of Guice modules must contain the binding of the
 * service interface and its implementations.</p>
 * <pre>
 * package my.application;
 * 
 * public class MyGuiceModule extends AbstractModule {
 *   protected void configure() {
 *     bind(MyService.class).to(MyServiceImpl.class);
 *   }
 * }</pre>
 * <p>The context listener must be configured into <code>web.xml
 * </code> file with the standard Guice configuration.</p>
 * <pre>
 * {@code
 * <filter>
 *   <filter-name>guiceFilter</filter-name>
 *   <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
 * </filter>
 * 
 * <filter-mapping>
 *   <filter-name>guiceFilter</filter-name>
 *   <url-pattern>/*</url-pattern>
 * </filter-mapping>
 * 
 * <listener>
 *   <listener-class>my.application.MyContextListener</listener-class>
 * </listener>}</pre>
 * <p>The context listener must be configured as a web application
 * listener.</p>
 * @author alessandro.simi@gmail.com
 */
public abstract class GuiceDpcServlet extends GuiceServletContextListener {

	@Override
	protected final Injector getInjector() {
		Module module = getGuiceModule();
		if(module == null) module = new DpcServletModule();
		else module = Modules.combine(module, new DpcServletModule());
		return Guice.createInjector(module);
	}
	
	/**
	 * @return The module of the combination of modules.
	 */
	protected abstract Module getGuiceModule(); 
	
	private class DpcServletModule extends ServletModule {

		@Override
		protected void configureServlets() {
			serve(getFilteredModule() + "/dpc").with(GuiceServlet.class);
		}
		
	}
	
	private String getFilteredModule() {
		String module = getGwtModule();
		if(module != null) {
			module = module.replace("\\", "/");
			if(!module.startsWith("/")) module = "/" + module;
			if(module.endsWith("/")) module.substring(0, module.length() - 1);
		} else {
			throw new RuntimeException("The getGwtModule() method mustn't return a null value.");
		}
		return module;
	}
	
	/**
	 * @return The url of the application.
	 */
	protected abstract String getGwtModule();
	
	/**
	 * <p>Adds interceptors at the remote call to execute
	 * code before and after a service is called.</p>
	 * @param interceptor type of Interceptor to add.
	 */
	@SuppressWarnings("unchecked")
	protected final <I extends MethodInterceptor> void addInterceptor(Class<I> interceptor) {
		if(interceptor != null) {
			interceptors.add((Class<MethodInterceptor>) interceptor);
		}
	}
		
	private static List<Class<MethodInterceptor>> interceptors = new ArrayList<Class<MethodInterceptor>>();
	
	@SuppressWarnings("serial") @Singleton
	public final static class GuiceServlet extends DpcServlet {

		@Inject private Injector injector;	
		
		@SuppressWarnings("unchecked")
		@Override
		final protected <I, S extends I> S getInstance(Class<I> interfaceClass) {
			return (S) injector.getInstance(interfaceClass);
		}

		@Override
		protected List<MethodInterceptor> getInterceptors() {
			List<MethodInterceptor> result = new ArrayList<MethodInterceptor>();
			for(Class<MethodInterceptor> clazz : interceptors) {
				result.add(injector.getInstance(clazz));
			}
			return result;
		}
		
	}

}
