# Introduction #
This page describes how to create server side services can be called by the client side.

## Simple Service ##
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
There are no limitations to the type can be used into the methods (parameters or return types), the only constraint is class used must be **serialiaziable** following the GWT RPC guide lines ([see the link](https://developers.google.com/web-toolkit/doc/latest/tutorial/RPC#serialize)).
## Complex Service ##
It is possible to create a client-side service from a complex server-side one creating an interface ad-hoc, especially if contains non-serializable classes.
```
public class ComplexServiceImpl implements ComplexService, SimpleService {

  public SerializableClass methodToCall(...) {
    return <MySerializableClass instance>;
  }

  public UnSerializableClass methodToHide(...) {
    return <UnSerializableClass instance>;
  }

}
```
The interface must contains only a subset of methods we want call from the client and that use **serializable** classes.
```
interface SimpleService extends Dpc {

  SerializableClass methodToCall(...);

}
```
The interface must extends the Dpc interface.