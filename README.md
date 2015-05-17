#Intoduction
Dpc framework simplifies clean-server communication in a GWT application allowing direct calls to server-side services without creating any communication structure as special interfaces, action classes or handlers.
The framework uses the (GWT RPC)[http://www.gwtproject.org/doc/latest/tutorial/RPC.html] communication technique and is inspired by (gwt-dispatch)[http://code.google.com/p/gwt-dispatch/] project.
##1. Create the service
Define a service with an interface and its implementation. The interface must extend DPC interface.
```
public interface GreetingService extends Dpc {
  String greetServer(String name) throws IllegalArgumentException;
}
```
And the implementation extends the interface.
```
public class GreetingServiceImpl implements GreetingService {
  public String greetServer(String name) throws IllegalArgumentException {
    return "Hello " + name "!";
  }
}
```
(see (this page)[http://code.google.com/p/gwt-dpc/wiki/CreateTheService] for more implementation details)

##2. Link the service interface to the implementation
Configure dpc servlet into the web descriptor file `web.xml` with or context injectors (as [http://www.springsource.org/ Spring] or [http://code.google.com/p/google-guice/ Guice]).
```
<web-app>

  <servlet>
    <servlet-name>greetServlet</servlet-name>
    <servlet-class>my.application.MyServlet</servlet-class>
  </servlet>
  
  <servlet-mapping>
    <servlet-name>greetServlet</servlet-name>
    <url-pattern>/myapplication/dpc</url-pattern>
  </servlet-mapping>
  
</web-app>
```
Bind the service interface with the implementation through Spring bean configuration, Guice binding or explicit association.
```
@SuppressWarnings("serial")
public class MyServlet extends SimpleDpcServlet {
  @Override
  public void configure() {
    add(GreetingService.class, new GreetingServiceImpl());
  }
}
```
(see [http://code.google.com/p/gwt-dpc/wiki/ServerConfiguration this page] for more details about Guice and Spring implementations)

##3. Call the service
Call `GWT.create()` method with the interface as parameter and result type.
```
GreetingService service = GWT.create(GreetingService.class);
```
Call interface methods into the `DpcExec.async()` method (first parameter) and read the result into the `AsyncCallback` instance (second parameter).
```
DpcExec.async(
  service.greetServer("John"),
  new AsyncCallback<String>() {
    public void onSuccess(String message) {
      // Message "Hello John!"
    }
    public void onFailure(Throwable caught) {
      // Error message
    }
  }
);
```
(see [http://code.google.com/p/gwt-dpc/wiki/CallTheService this page] for more details)
