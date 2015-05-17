# Introduction #
The service methods can be called directly from the client and the response is asynchronously managed by the `DpcExec.async()` method.

## Create and Call ##
Call `GWT.create()` method with the interface as parameter and result type to generate an instance of the service.
```
GreetingService service = GWT.create(GreetingService.class);
```
Use the instance to call methods into the `DpcExec.async()` method (first parameter) and read the result into the `AsyncCallback` instance (second parameter).
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
The return type of the interface method (e.g. `greetServer("John")`) is the same of the input parameter of `onSuccess()` method.