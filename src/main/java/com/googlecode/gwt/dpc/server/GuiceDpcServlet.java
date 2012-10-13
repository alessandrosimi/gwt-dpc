package com.googlecode.gwt.dpc.server;

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
 *   public String getGuiceModule() {
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
 * &lt;filter&gt;
 *   &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;com.google.inject.servlet.GuiceFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * 
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * 
 * &lt;listener&gt;
 *   &lt;listener-class&gt;my.application.MyContextListener&lt;/listener-class&gt;
 * &lt;/listener&gt;</pre>
 * <p>The context listener must be configured as a web application
 * listener.</p>
 * @author alessandro.simi@gmail.com
 */
public abstract class GuiceDpcServlet extends GuiceServletContextListener {

	@Override
	final protected Injector getInjector() {
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
			throw new RuntimeException("The getGwtModule() methos mustn't return a null value.");
		}
		return module;
	}
	
	/**
	 * @return The url of the application.
	 */
	protected abstract String getGwtModule();
	
	@SuppressWarnings("serial") @Singleton
	static public class GuiceServlet extends DpcServlet {

		@Inject private Injector injector;	
		
		@SuppressWarnings("unchecked")
		@Override
		final protected <I, S extends I> S getInstance(Class<I> interfaceClass) {
			return (S) injector.getInstance(interfaceClass);
		}
		
	}

}
