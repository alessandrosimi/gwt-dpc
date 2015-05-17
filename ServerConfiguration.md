# Introduction #
The server side side response is managed by one single servlet. As the [gwt-dispatch](http://code.google.com/p/gwt-dispatch/) framework, the client has one single access point (a servlet) to the server and a sort of dispatcher calls the correct service.

## Server Configuration (Simple) ##
The first step is specify the dpc servlet into the web descriptor file `web.xml` with or context injectors (as [Spring](http://www.springsource.org/) or [Guice](http://code.google.com/p/google-guice/)). The url-pattern must be the gwt module name followed by **/dpc**.
The configuration depends if we are using context injectors or not. The following example uses a simple servlet that doesn't require a context manager.
```
<web-app>

  <servlet>
    <servlet-name>myServlet</servlet-name>
    <servlet-class>my.application.MyServlet</servlet-class>
  </servlet>
  
  <servlet-mapping>
    <servlet-name>myServlet</servlet-name>
    <url-pattern>/myapplication/dpc</url-pattern>
  </servlet-mapping>
  
</web-app>
```
Bind the service interface with the implementation through Spring bean configuration, Guice binding or explicit association. The example of `web.xml` refers to `MyServlet` class that extends the `SimpleDpcServlet` servlet.
```
@SuppressWarnings("serial")
public class MyServlet extends SimpleDpcServlet {
  @Override
  public void configure() {
    add(MyService.class, new MyServiceImpl());
  }
}
```
Into the `configure()` method we can specify the link between the interfaces and their implementation calling the `add()` method.

## Guice Configuration ##
Configure the web descriptor file `web.xml` in the standard way specifying the context listener (e.g. `MyContextListener`).
```
<web-app>
  
  <filter>
    <filter-name>guiceFilter</filter-name>
    <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
  </filter>
 
  <filter-mapping>
    <filter-name>guiceFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
 
  <listener>
    <listener-class>my.application.MyContextListener</listener-class>
  </listener>

</web-app>
```
The context listener must extends `GuiceDpcServlet` abstract class and implementing two methods: `getGwtModule()` and `getGuiceModule()`.
```
public class MyContextListener extends GuiceDpcServlet {

  public String getGwtModule() {
    return "myapplication";
  }

  public Module getGuiceModule() {
    return new MyGuiceModule();
  }

}
```
The first one returns the module name of the GWT application and the second one the Guice module(s) (e.g. `MyGuiceModule`).
The modules contain the binding between the service interface and its implementation.
```
public class MyGuiceModule extends AbstractModule {

  protected void configure() {
    bind(MyService.class).to(MyServiceImpl.class);
  }

}
```
A Guice application is formed by several modules, so it is possible to use `Modules.combine()` to combine two or more methods.

## Spring Configuration ##
...